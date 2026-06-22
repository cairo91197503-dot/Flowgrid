package com.flowgrid.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.flowgrid.BuildConfig
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val daltonicMode by viewModel.daltonicMode.collectAsState()
    val isPro by viewModel.isPro.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Voltar", color = DarkText, fontWeight = FontWeight.Bold)
            }
            Text("Configurações", style = MaterialTheme.typography.titleLarge, color = DarkText, modifier = Modifier.padding(start = 16.dp))
        }

        // Seção: Visual
        Text("Visual", style = MaterialTheme.typography.titleMedium, color = DarkText.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 8.dp))
        Surface(
            color = androidx.compose.ui.graphics.Color.White,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Daltônico", style = MaterialTheme.typography.bodyLarge, color = DarkText)
                Switch(
                    checked = daltonicMode,
                    onCheckedChange = { viewModel.setDaltonicMode(it) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Seção: Conta Pro
        Text("Conta Pro", style = MaterialTheme.typography.titleMedium, color = DarkText.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 8.dp))
        if (isPro) {
            Surface(
                color = Jade.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✓", color = Jade, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("FlowGrid Pro ativo", color = Jade, fontWeight = FontWeight.Bold)
                }
            }
            Button(
                onClick = { 
                    viewModel.billingManager.restorePurchases(context as? Activity)
                    Toast.makeText(context, "Compras restauradas e verificadas.", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Earth),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Restaurar Compras", color = DarkText)
            }
        } else {
            Button(
                onClick = { navController.navigate("paywall") },
                colors = ButtonDefaults.buttonColors(containerColor = Jade),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Obter FlowGrid Pro →", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Seção: Suporte
        Text("Suporte", style = MaterialTheme.typography.titleMedium, color = DarkText.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 8.dp))
        Surface(
            color = androidx.compose.ui.graphics.Color.White,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://flowgrid.app/privacidade"))
                            context.startActivity(intent)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Policy, contentDescription = null, tint = DarkText)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Política de Privacidade", style = MaterialTheme.typography.bodyLarge, color = DarkText, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = DarkText.copy(alpha = 0.5f))
                }
                
                Divider(color = Earth.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.flowgrid")))
                            } catch (e: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.flowgrid")))
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.RateReview, contentDescription = null, tint = DarkText)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Avaliar o app", style = MaterialTheme.typography.bodyLarge, color = DarkText, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = DarkText.copy(alpha = 0.5f))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Seção: Sobre
        Text(
            text = "Versão ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkText.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
