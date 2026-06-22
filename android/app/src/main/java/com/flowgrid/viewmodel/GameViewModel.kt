package com.flowgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowgrid.billing.BillingManager
import com.flowgrid.data.DataStoreManager
import com.flowgrid.data.GameResult
import com.flowgrid.data.GameResultDao
import com.flowgrid.engine.LevelGenerator
import com.flowgrid.engine.PathValidator
import com.flowgrid.model.GameState
import com.flowgrid.model.GridCell
import com.flowgrid.model.Level
import com.flowgrid.model.PipeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val dataStoreManager: DataStoreManager,
    val billingManager: BillingManager
) : ViewModel() {

    private val _state = MutableStateFlow(GameState(level = null))
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _unconnectedCells = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val unconnectedCells: StateFlow<List<Pair<Int, Int>>> = _unconnectedCells.asStateFlow()
    
    val daltonicMode = dataStoreManager.daltonicMode

    fun initDaily() {
        val today = LocalDate.now()
        val seed = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth
        _state.value = GameState(level = LevelGenerator.generate(seed, 6), isDaily = true)
        validateGrid(false)
    }

    fun initFree(seed: Int?) {
        val finalSeed = seed ?: (0..1000000).random()
        _state.value = GameState(level = LevelGenerator.generate(finalSeed, 5), isDaily = false)
        validateGrid(false)
    }

    fun rotatePiece(x: Int, y: Int) {
        val currentState = _state.value
        if (currentState.isWon || currentState.level == null) return

        val level = currentState.level
        val cell = level.grid[y][x]
        if (cell.type == PipeType.EMPTY || cell.fixed) return

        // 2. Criar uma cópia profunda do grid para rotacionar
        val newGrid = Array(level.size) { r ->
            Array(level.size) { c ->
                level.grid[r][c].copy()
            }
        }
        
        newGrid[y][x] = newGrid[y][x].copy(rotation = (cell.rotation + 1) % 4)
        val newLevel = level.copy(grid = newGrid)

        _state.update { it.copy(level = newLevel, moves = it.moves + 1) }
        
        validateGrid(true)
    }

    fun verify() {
        val result = validateGrid(false)
        _unconnectedCells.value = result.unconnectedCells
    }

    private fun validateGrid(checkWin: Boolean): com.flowgrid.engine.ValidationResult {
        val level = _state.value.level ?: return com.flowgrid.engine.ValidationResult(false, emptySet(), emptyList())
        
        // Criar uma cópia profunda para não modificar o estado atual diretamente antes da emissão
        val newGrid = Array(level.size) { r ->
            Array(level.size) { c ->
                level.grid[r][c].copy()
            }
        }
        
        val result = PathValidator.validate(newGrid)
        val newLevel = level.copy(grid = newGrid)
        
        _state.update { it.copy(level = newLevel) }
        
        if (checkWin && result.isSolved && !_state.value.isWon) {
            _state.update { it.copy(isWon = true) }
            saveResult()
        }
        
        return result
    }

    private fun saveResult() {
        val currentState = _state.value
        val level = currentState.level ?: return
        
        viewModelScope.launch {
            val today = LocalDate.now()
            val todayInt = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth
            
            val result = GameResult(
                date = todayInt.toLong(),
                mode = if (currentState.isDaily) "daily" else "free",
                seed = level.seed,
                moves = currentState.moves,
                solved = true
            )
            gameResultDao.insert(result)
            
            if (currentState.isDaily) {
                dataStoreManager.updateStreak(todayInt)
            }
        }
    }
}
