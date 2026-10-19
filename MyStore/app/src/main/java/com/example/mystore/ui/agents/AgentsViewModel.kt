package com.example.mystore.ui.agents

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystore.data.model.AgentDto
import com.example.mystore.data.repository.ValorantRepository
import kotlinx.coroutines.launch

sealed interface AgentsUiState {
    object Loading : AgentsUiState
    data class Success(val agents: List<AgentDto>) : AgentsUiState
    data class Error(val message: String) : AgentsUiState
}

class AgentsViewModel : ViewModel() {
    private val repository = ValorantRepository()
    var uiState: AgentsUiState by mutableStateOf(AgentsUiState.Loading)
        private set

    init {
        fetchAgents()
    }

    fun fetchAgents() {
        viewModelScope.launch {
            uiState = AgentsUiState.Loading
            try {
                val list = repository.getAgents()
                uiState = AgentsUiState.Success(list)
            } catch (e: Exception) {
                uiState = AgentsUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}