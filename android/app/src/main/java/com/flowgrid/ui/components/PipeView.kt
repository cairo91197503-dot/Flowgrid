package com.flowgrid.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.flowgrid.model.GridCell
import com.flowgrid.model.PipeType
import com.flowgrid.ui.theme.Earth
import com.flowgrid.ui.theme.Jade
import com.flowgrid.ui.theme.Terracotta
import com.flowgrid.ui.theme.Water

@Composable
fun PipeView(
    cell: GridCell,
    daltonicMode: Boolean,
    isUnconnectedError: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = cell.rotation * 90f,
        animationSpec = tween(durationMillis = 200),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Earth.copy(alpha = 0.1f))
            .clickable(enabled = !cell.fixed && cell.type != PipeType.EMPTY, onClick = onClick)
            .then(
                if (isUnconnectedError) {
                    Modifier.border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                } else Modifier
            )
            .graphicsLayer {
                rotationZ = rotation
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val pipeStroke = Stroke(width = w * 0.24f, cap = StrokeCap.Square)
            val waterStroke = Stroke(
                width = w * 0.12f,
                cap = StrokeCap.Square,
                pathEffect = if (daltonicMode) PathEffect.dashPathEffect(floatArrayOf(w * 0.1f, w * 0.1f), 0f) else null
            )

            when (cell.type) {
                PipeType.STRAIGHT -> {
                    drawLine(
                        color = Terracotta,
                        start = Offset(w / 2, 0f),
                        end = Offset(w / 2, h),
                        strokeWidth = pipeStroke.width,
                        cap = pipeStroke.cap
                    )
                    if (cell.hasWater) {
                        drawLine(
                            color = Water,
                            start = Offset(w / 2, -2f),
                            end = Offset(w / 2, h + 2f),
                            strokeWidth = waterStroke.width,
                            cap = waterStroke.cap,
                            pathEffect = waterStroke.pathEffect
                        )
                    }
                }
                PipeType.CURVE -> {
                    val path = Path().apply {
                        moveTo(w / 2, 0f)
                        quadraticBezierTo(w / 2, h / 2, w, h / 2)
                    }
                    drawPath(path, color = Terracotta, style = pipeStroke)
                    if (cell.hasWater) {
                        val waterPath = Path().apply {
                            moveTo(w / 2, -2f)
                            quadraticBezierTo(w / 2, h / 2, w + 2f, h / 2)
                        }
                        drawPath(path = waterPath, color = Water, style = waterStroke)
                    }
                }
                PipeType.SOURCE -> {
                    drawLine(
                        color = Terracotta,
                        start = Offset(w / 2, h / 2),
                        end = Offset(w / 2, h),
                        strokeWidth = pipeStroke.width,
                        cap = pipeStroke.cap
                    )
                    drawCircle(color = Terracotta, radius = w * 0.28f, center = Offset(w / 2, h / 2))
                    if (cell.hasWater) {
                        drawLine(
                            color = Water,
                            start = Offset(w / 2, h / 2),
                            end = Offset(w / 2, h + 2f),
                            strokeWidth = waterStroke.width,
                            cap = waterStroke.cap,
                            pathEffect = waterStroke.pathEffect
                        )
                        drawCircle(color = Water, radius = w * 0.16f, center = Offset(w / 2, h / 2))
                    }
                    drawCircle(color = Color.White, radius = w * 0.06f, center = Offset(w / 2, h / 2))
                }
                PipeType.SINK -> {
                    drawLine(
                        color = Terracotta,
                        start = Offset(w / 2, 0f),
                        end = Offset(w / 2, h / 2),
                        strokeWidth = pipeStroke.width,
                        cap = pipeStroke.cap
                    )
                    drawCircle(color = Terracotta, radius = w * 0.28f, center = Offset(w / 2, h / 2))
                    drawCircle(color = Water, radius = w * 0.28f, center = Offset(w / 2, h / 2), style = Stroke(width = w * 0.04f))
                    if (cell.hasWater) {
                        drawLine(
                            color = Water,
                            start = Offset(w / 2, -2f),
                            end = Offset(w / 2, h / 2),
                            strokeWidth = waterStroke.width,
                            cap = waterStroke.cap,
                            pathEffect = waterStroke.pathEffect
                        )
                        drawCircle(color = Water, radius = w * 0.16f, center = Offset(w / 2, h / 2))
                    }
                }
                PipeType.EMPTY -> {}
            }
        }
    }
}
