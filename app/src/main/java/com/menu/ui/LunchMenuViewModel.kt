package com.menu.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.menu.LunchMenuApplication
import com.menu.data.LunchMenuRepository
import com.menu.model.LunchMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface LunchMenuUiState {
    object Loading : LunchMenuUiState
    data class Success(val lunchMenu: List<List<LunchMenu>>) : LunchMenuUiState
    object Error : LunchMenuUiState
}
class LunchMenuViewModel(
    private val lunchMenuRepository: LunchMenuRepository
): ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var lunchMenuUiState: LunchMenuUiState by mutableStateOf(LunchMenuUiState.Loading)
        private set

    /**
     * Call getLunchMenu() on init so we can display status immediately.
     */
    init {
        getLunchMenu()
    }

    fun getLunchMenu() {
        viewModelScope.launch {
            lunchMenuUiState = LunchMenuUiState.Loading
            withContext(Dispatchers.IO) {
                try {
                    val lunchMenuLists = lunchMenuRepository.getLunchMenu()
                    if (lunchMenuLists != null) {
                        lunchMenuUiState = LunchMenuUiState.Success(lunchMenuLists)
                    } else {
                        lunchMenuUiState = LunchMenuUiState.Error
                    }
                } catch (e: IOException) {
                    lunchMenuUiState = LunchMenuUiState.Error
                } catch (e: Exception) {
                    println("Unexpected error fetching lunch menu: ${e.message}")
                    lunchMenuUiState = LunchMenuUiState.Error
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LunchMenuApplication)
                val lunchMenuRepository = application.container.lunchMenuRepository
                LunchMenuViewModel(lunchMenuRepository = lunchMenuRepository)
            }
        }
    }
}