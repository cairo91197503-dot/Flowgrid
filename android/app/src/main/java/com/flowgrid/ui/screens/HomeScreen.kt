package com.flowgrid.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.flowgrid.ads.BannerAd
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.ui.theme.Terracotta
import com.flowgrid.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentStreak by viewModel.currentStreak.collectAsState()
    val isDailyPlayed by viewModel.isDailyPlayed.collectAsState()
    val isPro by viewModel.billingManager.isPro.collectAsState()
    
    var showChallengeDialog by remember { mutableStateOf(false) }
    var challengeCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("FlowGrid", style = MaterialTheme.typography.displayLarge, color = DarkText)
            Text("JARDIM DE PEDRA", style = MaterialTheme.typography.bodySmall, color = Earth, fontWeight = FontWeight.Bold, letterSpacing = 2.dp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                color = Earth.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.large
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$currentStreak day streak", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { navController.navigate("game/daily") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDailyPlayed) Earth.copy(alpha = 0.3f) else Jade,
                    contentColor = if (isDailyPlayed) DarkText.copy(alpha=0.7f) else MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Text(if (isDailyPlayed) "Rejogar Nível do Dia" else "Jogar Nível do Dia", style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { navController.navigate("game/free") },
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Text("Modo Livre", style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { navController.navigate("settings") }) {
                    Text("Configurações", color = DarkText)
                }
                TextButton(onClick = { showChallengeDialog = true }) {
                    Text("Desafiar", color = DarkText)
                }
                if (!isPro) {
                    TextButton(onClick = { navController.navigate("paywall") }) {
                        Text("⭐ PRO", color = Terracotta, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (!isPro) {
            BannerAd()
        }
    }

    if (showChallengeDialog) {
        AlertDialog(
            onDismissRequest = { showChallengeDialog = false },
            title = { Text("Inserir Código") },
            text = {
                OutlinedTextField(
                    value = challengeCode,
                    onValueChange = { challengeCode = it },
                    label = { Text("Código Numérico") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showChallengeDialog = false
                    val seed = challengeCode.toIntOrNull()
                    if (seed != null) {
                        navController.navigate("game/challenge?seed=$seed")
                    } else {
                        Toast.makeText(context, "Código inválido", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Jogar") }
            },
            dismissButton = {
                TextButton(onClick = { showChallengeDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
