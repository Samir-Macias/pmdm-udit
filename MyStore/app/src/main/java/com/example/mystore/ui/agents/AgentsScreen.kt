package com.example.mystore.ui.agents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mystore.data.model.AbilityDto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.mystore.data.model.AgentDto

@Composable
fun AgentsScreen(
    viewModel: AgentsViewModel = viewModel(),
    onBackPress: () -> Unit = {}
) {
    var selectedAgent by remember { mutableStateOf<AgentDto?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1923))
    ) {
        // Título superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AGENTES",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = Color(0xFFFF4655)
            )
        }

        when (val state = viewModel.uiState) {
            is AgentsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF4655))
                }
            }
            is AgentsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is AgentsUiState.Success -> {
                AgentList(
                    agents = state.agents,
                    onAgentClick = { agent -> selectedAgent = agent }
                )
            }
        }
    }

    // Pop-up con la animación integrada
    selectedAgent?.let { agent ->
        AgentDetailDialog(
            agent = agent,
            onDismiss = { selectedAgent = null }
        )
    }
}

@Composable
fun AgentList(agents: List<AgentDto>, onAgentClick: (AgentDto) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 340.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(agents) { agent ->
            AgentCard(agent = agent, onClick = { onAgentClick(agent) })
        }
    }
}

@Composable
fun AgentCard(agent: AgentDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2326)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen animada en la tarjeta
                InteractiveAgentImage(
                    imageUrl = agent.fullPortrait ?: agent.displayIcon,
                    displayName = agent.displayName,
                    imageSize = 80.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = agent.displayName.uppercase(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color.White
                    )

                    agent.role?.let { role ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFF4655).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            role.displayIcon?.let { iconUrl ->
                                AsyncImage(
                                    model = iconUrl,
                                    contentDescription = role.displayName,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = role.displayName,
                                color = Color(0xFFFF4655),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = agent.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Componente reutilizable con la animación 3D e interactiva
@Composable
fun InteractiveAgentImage(
    imageUrl: String,
    displayName: String,
    imageSize: Dp = 80.dp,
    height: Dp? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f)
    )
    val rotationY by animateFloatAsState(
        targetValue = if (isPressed) 12f else 0f,
        animationSpec = spring(dampingRatio = 0.5f)
    )

    val modifier = if (height != null) {
        Modifier
            .fillMaxWidth()
            .height(height)
    } else {
        Modifier.size(imageSize)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFF4655), Color(0xFF0F1923))
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = displayName,
            contentScale = contentScale,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.rotationY = rotationY
                    cameraDistance = 12f * density
                }
        )
    }
}

// POP-UP DETALLADO
@Composable
fun AgentDetailDialog(agent: AgentDto, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1F2326),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen animada interactiva dentro del Pop-up
                InteractiveAgentImage(
                    imageUrl = agent.fullPortrait ?: agent.displayIcon,
                    displayName = agent.displayName,
                    height = 220.dp,
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = agent.displayName.uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                agent.role?.let { role ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ROL: ${role.displayName.uppercase()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4655)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = agent.description,
                    fontSize = 13.sp,
                    color = Color.LightGray
                )

                // Lista completa de habilidades en el Pop-up
                agent.abilities?.filter { it.displayIcon != null }?.let { abilities ->
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "HABILIDADES",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    abilities.forEach { ability ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2B2F33),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                                    AsyncImage(
                                        model = ability.displayIcon,
                                        contentDescription = ability.displayName,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = ability.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                ability.description?.let { desc ->
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4655)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CERRAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

