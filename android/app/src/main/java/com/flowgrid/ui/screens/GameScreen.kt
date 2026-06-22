package com.flowgrid.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.flowgrid.ads.BannerAd
import com.flowgrid.audio.SoundManager
import com.flowgrid.model.PipeType
import com.flowgrid.ui.components.PipeView
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.ui.theme.Terracotta
import com.flowgrid.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    navController: NavController,
    mode: String,
    seed: Int? = null,
    viewModel: GameViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val daltonicMode by viewModel.daltonicMode.collectAsState(initial = false)
    val dicaCount by viewModel.dicaCount.collectAsState(initial = 3)
    val isPro by viewModel.isPro.collectAsState()
    val dicasIlimitadas by viewModel.dicasIlimitadas.collectAsState(initial = false)
    val unconnectedCells by viewModel.unconnectedCells.collectAsState()

    var highlightedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by pulseAnim.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    LaunchedEffect(highlightedCell) {
        if (highlightedCell != null) {
            delay(2000)
            highlightedCell = null
        }
    }

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    LaunchedEffect(Unit) {
        if (mode == "daily") {
            viewModel.initDaily()
        } else {
            viewModel.initFree(seed)
        }
    }

    LaunchedEffect(state.isWon) {
        if (state.isWon) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 50, 50, 30), -1))
            SoundManager.playVictory()
            delay(500)
            navController.navigate("victory/${if (state.isDaily) "daily" else "free"}/${state.level?.seed ?: 0}/${state.moves}") {
                popUpTo("home")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Voltar", color = DarkText, fontWeight = FontWeight.Bold)
            }
            Text(
                text = if (mode == "daily") "Nível do Dia" else "Modo Livre",
                style = MaterialTheme.typography.titleLarge,
                color = DarkText
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (dicaCount == 0 && !dicasIlimitadas) {
                        navController.navigate("paywall")
                    } else {
                        scope.launch {
                            val target = viewModel.usarDica()
                            if (target != null) {
                                highlightedCell = target
                            }
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = "Dica",
                        tint = if (dicaCount > 0 || dicasIlimitadas) Terracotta else Color.Gray
                    )
                }
                Surface(
                    color = Earth.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (dicasIlimitadas) "∞" else "$dicaCount",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Earth.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${state.moves} mov",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Grid
        state.level?.let { level ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (y in 0 until level.size) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            for (x in 0 until level.size) {
                                val cell = level.grid[y][x]
                                val isUnconnectedError = unconnectedCells.any { it.first == x && it.second == y }
                                val isHighlighted = highlightedCell?.first == x && highlightedCell?.second == y
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(2.dp)
                                        .then(
                                            if (isHighlighted) Modifier.border(3.dp, Jade.copy(alpha = borderAlpha), MaterialTheme.shapes.small)
                                            else Modifier
                                        )
                                ) {
                                    PipeView(
                                        cell = cell,
                                        daltonicMode = daltonicMode,
                                        isUnconnectedError = isUnconnectedError,
                                        onClick = {
                                            if (!state.isWon && !cell.fixed && cell.type != PipeType.EMPTY) {
                                                if (isHighlighted) highlightedCell = null
                                                SoundManager.playClick()
                                                vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
                                                viewModel.rotatePiece(x, y)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.verify() },
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
            ) {
                Text("Verificar")
            }
        }

        if (!isPro) {
            BannerAd()
        } else {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
