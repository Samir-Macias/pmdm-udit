package com.example.mystore.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onNavigateToAgents: () -> Unit,
    onNavigateToMaps: () -> Unit = {},
    onNavigateToWeapons: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1923))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "VALORANT",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF4655),
            letterSpacing = 2.sp
        )
        Text(
            text = "WIKI & AGENTS",
            fontSize = 14.sp,
            color = Color.LightGray,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botón Agentes
        MenuButton(text = "AGENTES / JUGADORES", onClick = onNavigateToAgents)

        Spacer(modifier = Modifier.height(16.dp))

        // Botones adicionales (para futuras secciones)
        MenuButton(text = "MAPAS (PRÓXIMAMENTE)", onClick = onNavigateToMaps, enabled = false)

        Spacer(modifier = Modifier.height(16.dp))

        MenuButton(text = "ARMAS (PRÓXIMAMENTE)", onClick = onNavigateToWeapons, enabled = false)
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1F2326),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF15181B),
            disabledContentColor = Color.Gray
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}