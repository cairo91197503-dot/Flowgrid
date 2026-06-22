package com.flowgrid.engine

import com.flowgrid.model.GridCell
import com.flowgrid.model.PipeType

data class ValidationResult(
    val isSolved: Boolean,
    val connectedCells: Set<Pair<Int, Int>>,
    val unconnectedCells: List<Pair<Int, Int>>
)

object PathValidator {

    private fun getNeighbors(c: GridCell): List<Int> {
        return when (c.type) {
            PipeType.STRAIGHT -> listOf((0 + c.rotation) % 4, (2 + c.rotation) % 4)
            PipeType.CURVE -> listOf((0 + c.rotation) % 4, (1 + c.rotation) % 4)
            PipeType.SOURCE -> listOf((2 + c.rotation) % 4)
            PipeType.SINK -> listOf((0 + c.rotation) % 4)
            PipeType.EMPTY -> emptyList()
        }
    }

    fun validate(grid: Array<Array<GridCell>>): ValidationResult {
        val size = grid.size
        // Reset water
        for (y in 0 until size) {
            for (x in 0 until size) {
                grid[y][x] = grid[y][x].copy(hasWater = false)
            }
        }

        var source: GridCell? = null
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (grid[y][x].type == PipeType.SOURCE) {
                    source = grid[y][x]
                    break
                }
            }
            if (source != null) break
        }

        if (source == null) return ValidationResult(false, emptySet(), emptyList())

        val queue = ArrayDeque<GridCell>()
        
        // Mark source
        grid[source.y][source.x] = grid[source.y][source.x].copy(hasWater = true)
        queue.add(grid[source.y][source.x])

        val connectedCells = mutableSetOf<Pair<Int, Int>>()

        while (queue.isNotEmpty()) {
            val c = queue.removeFirst()
            connectedCells.add(Pair(c.x, c.y))
            val dirs = getNeighbors(c)
            for (d in dirs) {
                val nx = c.x + if (d == 1) 1 else if (d == 3) -1 else 0
                val ny = c.y + if (d == 2) 1 else if (d == 0) -1 else 0

                if (nx in 0 until size && ny in 0 until size) {
                    val n = grid[ny][nx]
                    if (n.type != PipeType.EMPTY && !n.hasWater) {
                        val oppDir = (d + 2) % 4
                        val nDirs = getNeighbors(n)
                        if (nDirs.contains(oppDir)) {
                            grid[ny][nx] = n.copy(hasWater = true)
                            queue.add(grid[ny][nx])
                        }
                    }
                }
            }
        }

        var totalPieces = 0
        var waterPieces = 0
        val unconnectedCells = mutableListOf<Pair<Int, Int>>()

        for (y in 0 until size) {
            for (x in 0 until size) {
                val c = grid[y][x]
                if (c.type != PipeType.EMPTY) {
                    totalPieces++
                    if (c.hasWater) {
                        waterPieces++
                    } else {
                        unconnectedCells.add(Pair(c.x, c.y))
                    }
                }
            }
        }

        val won = totalPieces == waterPieces

        return ValidationResult(won, connectedCells, unconnectedCells)
    }
}
