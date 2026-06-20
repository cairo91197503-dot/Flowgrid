package com.flowgrid.ui.screens

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flowgrid.engine.LevelGenerator
import com.flowgrid.engine.PathValidator
import com.flowgrid.model.PipeType
import com.flowgrid.ui.theme.DarkText
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Sand
import com.flowgrid.ui.theme.Terracotta
import com.flowgrid.ui.theme.Water
import com.flowgrid.util.ShareHelper

@Composable
fun VictoryScreen(
    navController: NavController,
    mode: String,
    seed: Int,
    moves: Int
) {
    val context = LocalContext.current
    
    // Generate the solved grid explicitly (scramble = false)
    val level = remember(seed) { 
        val l = LevelGenerator.generate(seed, if (mode == "daily") 6 else 5, scramble = false)
        PathValidator.validate(l.grid)
        l 
    }

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val progress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "water_flow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / level.size
                
                // Draw base pipes
                val pipeStroke = Stroke(width = cellSize * 0.24f, cap = StrokeCap.Square)
                for (y in 0 until level.size) {
                    for (x in 0 until level.size) {
                        val cell = level.grid[y][x]
                        
                        // We use the rotation from the solved cell
                        val cx = x * cellSize + cellSize / 2
                        val cy = y * cellSize + cellSize / 2
                        
                        withTransform({
                            translate(left = cx, top = cy)
                            rotate(degrees = cell.rotation * 90f)
                            translate(left = -cellSize / 2, top = -cellSize / 2)
                        }) {
                            val w = cellSize
                            val h = cellSize
                            
                            // Draw grey/earth background for pipe holder just for visual similarity
                            drawRoundRect(
                                color = Earth.copy(alpha=0.1f),
                                size = androidx.compose.ui.geometry.Size(w-4, h-4), // approximate
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                            )
                            
                            when (cell.type) {
                                PipeType.STRAIGHT -> {
                                    drawLine(Terracotta, androidx.compose.ui.geometry.Offset(w/2, 0f), androidx.compose.ui.geometry.Offset(w/2, h), strokeWidth = pipeStroke.width, cap = pipeStroke.cap)
                                }
                                PipeType.CURVE -> {
                                    val cp = Path().apply {
                                        moveTo(w/2, 0f)
                                        quadraticBezierTo(w/2, h/2, w, h/2)
                                    }
                                    drawPath(cp, Terracotta, style = pipeStroke)
                                }
                                PipeType.SOURCE -> {
                                    drawLine(Terracotta, androidx.compose.ui.geometry.Offset(w/2, h/2), androidx.compose.ui.geometry.Offset(w/2, h), strokeWidth = pipeStroke.width, cap = pipeStroke.cap)
                                    drawCircle(Terracotta, w*0.28f, center = androidx.compose.ui.geometry.Offset(w/2, h/2))
                                    drawCircle(Color.White, w*0.06f, center = androidx.compose.ui.geometry.Offset(w/2, h/2))
                                }
                                PipeType.SINK -> {
                                    drawLine(Terracotta, androidx.compose.ui.geometry.Offset(w/2, 0f), androidx.compose.ui.geometry.Offset(w/2, h/2), strokeWidth = pipeStroke.width, cap = pipeStroke.cap)
                                    drawCircle(Terracotta, w*0.28f, center = androidx.compose.ui.geometry.Offset(w/2, h/2))
                                    drawCircle(Water, w*0.28f, center = androidx.compose.ui.geometry.Offset(w/2, h/2), style = Stroke(width = w*0.04f))
                                }
                                PipeType.EMPTY -> {}
                            }
                        }
                    }
                }
                
                // Overlay Water incrementally
                val waterStroke = Stroke(width = cellSize * 0.12f, cap = StrokeCap.Square)
                for (y in 0 until level.size) {
                    for (x in 0 until level.size) {
                        val cell = level.grid[y][x]
                        if (cell.hasWater) {
                             // A rough approximation to flow: cells closer to 0,0 appear first
                            val cellDist = (x + y) / (level.size * 2f) 
                            if (progress > cellDist) {
                                val cellProgress = ((progress - cellDist) * 5f).coerceIn(0f, 1f)
                                
                                val cx = x * cellSize + cellSize / 2
                                val cy = y * cellSize + cellSize / 2
                                
                                withTransform({
                                    translate(left = cx, top = cy)
                                    rotate(degrees = cell.rotation * 90f)
                                    translate(left = -cellSize / 2, top = -cellSize / 2)
                                }) {
                                    val w = cellSize
                                    val h = cellSize
                                    when (cell.type) {
                                        PipeType.STRAIGHT -> {
                                            val len = h * cellProgress
                                            drawLine(Water, androidx.compose.ui.geometry.Offset(w/2, 0f), androidx.compose.ui.geometry.Offset(w/2, len), strokeWidth = waterStroke.width, cap = waterStroke.cap)
                                        }
                                        PipeType.CURVE -> {
                                            // Simplification: alpha fade for curved pieces during flow to save complicated PathMeasure per cell
                                            val cp = Path().apply {
                                                moveTo(w/2, -2f)
                                                quadraticBezierTo(w/2, h/2, w+2f, h/2)
                                            }
                                            drawPath(cp, Water.copy(alpha = Water.alpha * cellProgress), style = waterStroke)
                                        }
                                        PipeType.SOURCE -> {
                                            drawCircle(Water, w*0.16f * cellProgress, center = androidx.compose.ui.geometry.Offset(w/2, h/2))
                                            val len = (h - h/2) * cellProgress
                                            drawLine(Water, androidx.compose.ui.geometry.Offset(w/2, h/2), androidx.compose.ui.geometry.Offset(w/2, h/2 + len), strokeWidth = waterStroke.width, cap = waterStroke.cap)
                                        }
                                        PipeType.SINK -> {
                                            val len = (h/2) * cellProgress
                                            drawLine(Water, androidx.compose.ui.geometry.Offset(w/2, 0f), androidx.compose.ui.geometry.Offset(w/2, len), strokeWidth = waterStroke.width, cap = waterStroke.cap)
                                            if (cellProgress > 0.8f) {
                                                drawCircle(Water, w*0.16f * ((cellProgress-0.8f)*5f), center = androidx.compose.ui.geometry.Offset(w/2, h/2))
                                            }
                                        }
                                        PipeType.EMPTY -> {}
                                    }
                                }
                            }
                        }
                    }
                }
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
        
        val shareText = "FlowGrid #${level.seed} 🌊\nResolvido em $moves movimentos!\nBaixe: https://play.google.com/store/apps/details?id=com.flowgrid"

        Button(
            onClick = {
                ShareHelper.shareGridImage(context, level, shareText)
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
