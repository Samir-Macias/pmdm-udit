package com.example.mystore.ui.stats

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystore.data.model.AccountDataDto
import com.example.mystore.data.model.MatchDto
import com.example.mystore.data.model.MmrDataDto
import com.example.mystore.data.repository.StatsRepository
import kotlinx.coroutines.launch

data class PlayerStatsUiData(
    val account: AccountDataDto,
    val mmr: MmrDataDto?,
    val matches: List<MatchDto>
)

sealed interface StatsUiState {
    object Idle : StatsUiState
    object Loading : StatsUiState
    data class Success(val data: PlayerStatsUiData) : StatsUiState
    data class Error(val message: String) : StatsUiState
}

class StatsViewModel : ViewModel() {
    private val repository = StatsRepository()
    var uiState: StatsUiState by mutableStateOf(StatsUiState.Idle)
        private set

    fun fetchPlayerStats(name: String, tag: String, defaultRegion: String = "eu") {
        if (name.isBlank() || tag.isBlank()) return

        viewModelScope.launch {
            uiState = StatsUiState.Loading
            try {
                // 1. Obtener datos de la cuenta
                val account = repository.getAccount(name, tag)
                val region = account.region.ifBlank { defaultRegion }

                // 2. Obtener Rango (MMR) e Historial
                val mmr = try { repository.getMmr(region, name, tag) } catch (_: Exception) { null }
                val matches = try { repository.getMatches(region, name, tag) } catch (_: Exception) { emptyList() }

                uiState = StatsUiState.Success(
                    PlayerStatsUiData(account = account, mmr = mmr, matches = matches)
                )
            } catch (e: Exception) {
                uiState = StatsUiState.Error(e.localizedMessage ?: "Error al obtener las estadísticas")
            }
        }
    }

    fun resetState() {
        uiState = StatsUiState.Idle
    }
}

