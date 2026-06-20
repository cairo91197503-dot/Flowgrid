package com.flowgrid.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.ui.theme.Terracotta

@Composable
fun VictoryScreen(
    navController: NavController,
    mode: String,
    seed: Int,
    moves: Int
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Jade,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("✓", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Nível Concluído!",
            style = MaterialTheme.typography.displayLarge,
            color = DarkText
        )
        Text(
            text = "Resolvido em $moves movimentos.",
            style = MaterialTheme.typography.titleLarge,
            color = Earth
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                val modeStr = if (mode == "daily") "Diário" else "Livre"
                val text = "FlowGrid $modeStr 🌊\nResolvido em $moves movimentos!\nBaixe agora: https://play.google.com/store/apps/details?id=com.flowgrid"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Compartilhar"))
            },
            colors = ButtonDefaults.buttonColors(containerColor = DarkText),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Compartilhar", color = Sand, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (mode == "free" || mode == "challenge") {
            Button(
                onClick = {
                    val text = "Desafio FlowGrid código: $seed\nSe ainda não tem o app, baixe: https://play.google.com/store/apps/details?id=com.flowgrid"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, text)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Desafiar Amigo"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Desafiar Amigo", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        TextButton(
            onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
        ) {
            Text("Voltar Início", color = DarkText)
        }
    }
}
