package com.flowgrid.engine

import com.flowgrid.model.GridCell
import com.flowgrid.model.Level
import com.flowgrid.model.PipeType

object LevelGenerator {
    
    // Simple PRNG to match deterministic behavior
    private class Mulberry32(private var state: Int) {
        fun nextDouble(): Double {
            state += -0x61C88647
            var t = state
            t = (t xor (t ushr 15)) * (t or 1)
            t = t xor (t + ((t xor (t ushr 7)) * (t or 61)))
            val res = (t xor (t ushr 14)).toUInt().toLong()
            return res.toDouble() / 4294967296.0
        }
        
        fun nextInt(bound: Int): Int {
            return (nextDouble() * bound).toInt()
        }
    }

    private data class Point(val x: Int, val y: Int)

    private fun getDir(from: Point, to: Point): Int {
        if (to.x - from.x == 1) return 1
        if (from.x - to.x == 1) return 3
        if (to.y - from.y == 1) return 2
        if (from.y - to.y == 1) return 0
        return 0
    }

    private fun buildPath(w: Int, h: Int, random: Mulberry32): List<Point> {
        while (true) {
            val path = mutableListOf(Point(0, 0))
            val visited = Array(h) { BooleanArray(w) }
            visited[0][0] = true
            var curr = path[0]

            while (curr.x != w - 1 || curr.y != h - 1) {
                val neighbors = listOf(
                    Point(curr.x, curr.y - 1),
                    Point(curr.x + 1, curr.y),
                    Point(curr.x, curr.y + 1),
                    Point(curr.x - 1, curr.y)
                ).filter { n -> n.x in 0 until w && n.y in 0 until h && !visited[n.y][n.x] }

                if (neighbors.isEmpty()) break
                
                // Shuffle using our random
                val shuffled = neighbors.toMutableList()
                for (i in shuffled.indices.reversed()) {
                    val j = random.nextInt(i + 1)
                    val temp = shuffled[i]
                    shuffled[i] = shuffled[j]
                    shuffled[j] = temp
                }
                
                val next = shuffled[0]
                visited[next.y][next.x] = true
                path.add(next)
                curr = next
            }

            if (curr.x == w - 1 && curr.y == h - 1) {
                if (path.size > (w * h) / 1.5) return path
            }
        }
    }

    fun generate(seed: Int, size: Int = 5, scramble: Boolean = true): Level {
        val random = Mulberry32(seed)
        val grid = Array(size) { y ->
            Array(size) { x ->
                GridCell(x, y, PipeType.EMPTY, 0, false, false)
            }
        }

        val path = buildPath(size, size, random)

        for (i in path.indices) {
            val pt = path[i]
            val c = grid[pt.y][pt.x]
            if (i == 0) {
                c.type = PipeType.SOURCE
                c.fixed = true
                val dir2 = getDir(pt, path[i + 1])
                c.rotation = (dir2 - 2 + 4) % 4
            } else if (i == path.size - 1) {
                c.type = PipeType.SINK
                c.fixed = true
                val dir1 = getDir(pt, path[i - 1])
                c.rotation = dir1
            } else {
                val d1 = getDir(pt, path[i - 1])
                val d2 = getDir(pt, path[i + 1])
                if (d1 % 2 == d2 % 2) {
                    c.type = PipeType.STRAIGHT
                    c.rotation = if (d1 % 2 == 0) 0 else 1
                } else {
                    c.type = PipeType.CURVE
                    for (r in 0 until 4) {
                        val rotConn = listOf((0 + r) % 4, (1 + r) % 4)
                        if (rotConn.contains(d1) && rotConn.contains(d2)) {
                            c.rotation = r
                            break
                        }
                    }
                }
            }
        }

        if (scramble) {
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val c = grid[y][x]
                    if (!c.fixed && c.type != PipeType.EMPTY) {
                        c.rotation = random.nextInt(4)
                    }
                }
            }
        }

        return Level(size, seed, grid)
    }
}
