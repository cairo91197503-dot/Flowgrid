package com.flowgrid.ui.screens

import android.app.Activity
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
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Sand
import com.flowgrid.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val daltonicMode by viewModel.daltonicMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Voltar", color = DarkText, fontWeight = FontWeight.Bold)
            }
            Text("Configurações", style = MaterialTheme.typography.titleLarge, color = DarkText, modifier = Modifier.padding(start = 16.dp))
        }

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
                    onCheckedChange = { viewModel.toggleDaltonicMode(it) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
    }
}
