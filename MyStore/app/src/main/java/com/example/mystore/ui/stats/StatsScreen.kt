package com.example.mystore.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mystore.data.model.MatchDto

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(),
    onBackPress: () -> Unit = {}
) {
    var nameInput by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1923))
    ) {
        // Encabezado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ESTADÍSTICAS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF4655)
            )
        }

        when (val state = viewModel.uiState) {
            is StatsUiState.Idle -> {
                SearchForm(
                    name = nameInput,
                    tag = tagInput,
                    onNameChange = { nameInput = it },
                    onTagChange = { tagInput = it },
                    onSearch = { viewModel.fetchPlayerStats(nameInput, tagInput) }
                )
            }
            is StatsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF4655))
                }
            }
            is StatsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(text = state.message, color = Color(0xFFFF4655), fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetState() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4655))
                        ) {
                            Text("NUEVA BÚSQUEDA")
                        }
                    }
                }
            }
            is StatsUiState.Success -> {
                StatsDashboard(
                    data = state.data,
                    onNewSearch = { viewModel.resetState() }
                )
            }
        }
    }
}

@Composable
fun SearchForm(
    name: String,
    tag: String,
    onNameChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CONSULTAR JUGADOR", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Riot ID (Ej: TenZ)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4655),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFFFF4655),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = tag,
            onValueChange = onTagChange,
            label = { Text("Tagline (Ej: NA1)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF4655),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFFFF4655),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSearch,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4655))
        ) {
            Text("BUSCAR ESTADÍSTICAS", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatsDashboard(data: PlayerStatsUiData, onNewSearch: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta del Jugador
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2326))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = data.account.card?.small,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${data.account.name} #${data.account.tag}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Nivel: ${data.account.accountLevel}", color = Color.LightGray, fontSize = 14.sp)
                        data.mmr?.currentData?.currentTierPatched?.let { rank ->
                            Text(text = "Rango: $rank", color = Color(0xFFFF4655), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNewSearch,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2F33)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("BUSCAR OTRO JUGADOR", color = Color.White)
            }
        }

        // Título Historial
        item {
            Text(text = "PARTIDAS RECIENTES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Lista de Partidas
        items(data.matches) { match ->
            MatchItem(match = match, playerName = data.account.name)
        }
    }
}

@Composable
fun MatchItem(match: MatchDto, playerName: String) {
    val player = match.players?.allPlayers?.find { it.name.equals(playerName, ignoreCase = true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2326))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = player?.assets?.agent?.small,
                    contentDescription = player?.character,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = match.metadata?.map ?: "Mapa", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = match.metadata?.mode ?: "Modo", color = Color.Gray, fontSize = 12.sp)
                }
            }

            player?.stats?.let { stats ->
                Text(
                    text = "${stats.kills} / ${stats.deaths} / ${stats.assists}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

