package com.flowgrid.model

enum class PipeType {
    STRAIGHT, CURVE, EMPTY, SOURCE, SINK
}

data class GridCell(
    val x: Int,
    val y: Int,
    val type: PipeType,
    val rotation: Int, // 0, 1, 2, 3 (each is 90 degrees)
    val fixed: Boolean,
    val hasWater: Boolean = false
)

data class Level(
    val size: Int,
    val seed: Int,
    val grid: Array<Array<GridCell>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Level
        if (size != other.size) return false
        if (seed != other.seed) return false
        if (!grid.contentDeepEquals(other.grid)) return false
        return true
    }
    
    override fun hashCode(): Int {
        var result = size
        result = 31 * result + seed
        result = 31 * result + grid.contentDeepHashCode()
        return result
    }
}

data class GameState(
    val level: Level?,
    val moves: Int = 0,
    val isWon: Boolean = false,
    val isDaily: Boolean = false
)
