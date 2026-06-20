package com.flowgrid.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // YYYYMMDD
    val mode: String, // "daily" or "free"
    val seed: Int,
    val moves: Int,
    val solved: Boolean
)

@Dao
interface GameResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: GameResult)

    @Query("SELECT * FROM game_results ORDER BY date DESC")
    fun getAllResults(): Flow<List<GameResult>>

    @Query("SELECT COUNT(*) FROM game_results WHERE solved = 1")
    fun getTotalSolved(): Flow<Int>
    
    @Query("SELECT SUM(moves) FROM game_results WHERE solved = 1")
    fun getTotalMoves(): Flow<Int?>
}

@Database(entities = [GameResult::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao
}
