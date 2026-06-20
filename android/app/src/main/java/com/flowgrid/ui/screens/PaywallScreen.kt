package com.flowgrid.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.viewmodel.SettingsViewModel

@Composable
fun PaywallScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isPro by viewModel.billingManager.isPro.collectAsState()

    if (isPro) {
        // Automatically go back if user becomes pro
        androidx.compose.runtime.LaunchedEffect(isPro) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⭐", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("FlowGrid Pro", style = MaterialTheme.typography.displayLarge, color = DarkText)
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            color = Earth.copy(alpha = 0.2f),
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Benefícios", fontWeight = FontWeight.Bold, color = DarkText, modifier = Modifier.padding(bottom = 16.dp))
                Text("• Sem anúncios", color = DarkText, modifier = Modifier.padding(bottom = 8.dp))
                Text("• Apoie o desenvolvedor", color = DarkText, modifier = Modifier.padding(bottom = 8.dp))
                Text("• Ajuda visual ilimitada", color = DarkText)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                val activity = context as? Activity
                if (activity != null) {
                    viewModel.billingManager.launchBillingFlow(activity)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Jade),
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            Text("Comprar Pro (R$ 4,90)", style = MaterialTheme.typography.titleLarge)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Continuar Grátis", color = DarkText)
        }
    }
}
