package com.flowgrid.util

import android.content.Context
import android.content.Intent
import android.graphics.*
import androidx.core.content.FileProvider
import com.flowgrid.model.GridCell
import com.flowgrid.model.Level
import com.flowgrid.model.PipeType
import java.io.File
import java.io.FileOutputStream

object ShareHelper {
    fun shareGridImage(context: Context, level: Level, text: String) {
        val size = 1000
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // bgColor: Sand
        canvas.drawColor(0xFFE8D5C4.toInt())
        
        val cellSize = size / level.size.toFloat()
        val paintPipe = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFC67A4B.toInt() // Terracotta
            strokeWidth = cellSize * 0.24f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.SQUARE
        }
        val paintWater = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0x993A9BD5.toInt() // Water
            strokeWidth = cellSize * 0.12f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.SQUARE
        }

        for (y in 0 until level.size) {
            for (x in 0 until level.size) {
                val cell = level.grid[y][x]
                drawCell(canvas, cell, x * cellSize, y * cellSize, cellSize, paintPipe, paintWater)
            }
        }

        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "flowgrid_share.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Compartilhar Vitória"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawCell(
        canvas: Canvas, cell: GridCell,
        left: Float, top: Float, size: Float,
        paintPipe: Paint, paintWater: Paint
    ) {
        canvas.save()
        canvas.translate(left + size / 2, top + size / 2)
        canvas.rotate(cell.rotation * 90f)
        canvas.translate(-size / 2, -size / 2)

        val w = size
        val h = size

        when (cell.type) {
            PipeType.STRAIGHT -> {
                canvas.drawLine(w / 2, 0f, w / 2, h, paintPipe)
                if (cell.hasWater) {
                    canvas.drawLine(w / 2, -2f, w / 2, h + 2f, paintWater)
                }
            }
            PipeType.CURVE -> {
                val path = Path().apply {
                    moveTo(w / 2, 0f)
                    quadTo(w / 2, h / 2, w, h / 2)
                }
                canvas.drawPath(path, paintPipe)
                if (cell.hasWater) {
                    canvas.drawPath(path, paintWater)
                }
            }
            PipeType.SOURCE -> {
                canvas.drawLine(w / 2, h / 2, w / 2, h, paintPipe)
                paintPipe.style = Paint.Style.FILL
                canvas.drawCircle(w / 2, h / 2, w * 0.28f, paintPipe)
                paintPipe.style = Paint.Style.STROKE
                if (cell.hasWater) {
                    canvas.drawLine(w / 2, h / 2, w / 2, h + 2f, paintWater)
                    paintWater.style = Paint.Style.FILL
                    canvas.drawCircle(w / 2, h / 2, w * 0.16f, paintWater)
                    paintWater.style = Paint.Style.STROKE
                }
            }
            PipeType.SINK -> {
                canvas.drawLine(w / 2, 0f, w / 2, h / 2, paintPipe)
                paintPipe.style = Paint.Style.FILL
                canvas.drawCircle(w / 2, h / 2, w * 0.28f, paintPipe)
                paintPipe.style = Paint.Style.STROKE
                if (cell.hasWater) {
                    canvas.drawLine(w / 2, -2f, w / 2, h / 2, paintWater)
                    paintWater.style = Paint.Style.FILL
                    canvas.drawCircle(w / 2, h / 2, w * 0.16f, paintWater)
                    paintWater.style = Paint.Style.STROKE
                }
            }
            PipeType.EMPTY -> {}
        }
        canvas.restore()
    }
}
