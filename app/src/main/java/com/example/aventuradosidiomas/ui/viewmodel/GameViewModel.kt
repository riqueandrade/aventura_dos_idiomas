package com.example.aventuradosidiomas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aventuradosidiomas.data.entity.Mission
import com.example.aventuradosidiomas.data.entity.User
import com.example.aventuradosidiomas.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            try {
                // Carregar usuário atual
                repository.getCurrentUser().collect { user ->
                    _uiState.update { it.copy(currentUser = user) }
                    // Carregar dados iniciais quando tiver um usuário
                    if (user != null) {
                        loadInitialData(user.id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao carregar dados. Tente novamente."))
            }
        }
    }

    private fun loadInitialData(userId: Long) {
        viewModelScope.launch {
            try {
                // Carregar categorias
                repository.getAllCategories().collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao carregar categorias. Tente novamente."))
            }
        }

        viewModelScope.launch {
            try {
                // Carregar missões desbloqueadas
                repository.getUnlockedMissions().collect { missions ->
                    _uiState.update { it.copy(unlockedMissions = missions) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao carregar missões. Tente novamente."))
            }
        }

        viewModelScope.launch {
            try {
                // Carregar progresso
                repository.getCompletedMissionsCount(userId).collect { count ->
                    _uiState.update { it.copy(completedMissions = count) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao carregar progresso. Tente novamente."))
            }
        }

        viewModelScope.launch {
            try {
                // Carregar pontuação total
                repository.getTotalScore(userId).collect { score ->
                    _uiState.update { it.copy(totalScore = score) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao carregar pontuação. Tente novamente."))
            }
        }
    }

    fun createUser(name: String, language: String) {
        viewModelScope.launch {
            try {
                repository.createUser(name, language)
                _events.emit(GameEvent.UserCreated)
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao criar usuário. Tente novamente."))
            }
        }
    }

    fun selectCategory(category: String) {
        viewModelScope.launch {
            try {
                repository.getMissionsByCategory(category).collect { missions ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedCategory = category,
                            currentCategoryMissions = missions,
                            currentMission = null // Limpar missão atual ao trocar de categoria
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(GameEvent.Error("Erro ao carregar missões da categoria. Tente novamente."))
            }
        }
    }

    fun startMission(missionId: Long) {
        viewModelScope.launch {
            repository.getMissionsByCategory(_uiState.value.selectedCategory ?: "").collect { missions ->
                val mission = missions.find { it.id == missionId }
                mission?.let { 
                    _uiState.update { it.copy(currentMission = mission) }
                }
            }
        }
    }

    fun completeMission(score: Int) {
        val currentUser = _uiState.value.currentUser
        val currentMission = _uiState.value.currentMission
        
        if (currentUser != null && currentMission != null) {
            viewModelScope.launch {
                try {
                    // Salvar progresso da missão
                    repository.completeMission(currentUser.id, currentMission.id, score)
                    
                    // Atualizar dados após completar a missão
                    loadInitialData(currentUser.id)
                    
                    // Recarregar missões da categoria atual
                    _uiState.value.selectedCategory?.let { category ->
                        repository.getMissionsByCategory(category).collect { missions ->
                            _uiState.update { it.copy(
                                currentMission = null,
                                currentCategoryMissions = missions
                            )}
                        }
                    }
                    
                    // Emitir evento de conclusão
                    _events.emit(GameEvent.MissionCompleted(score))
                } catch (e: Exception) {
                    e.printStackTrace()
                    _events.emit(GameEvent.Error("Error al completar la misión. Inténtalo de nuevo."))
                }
            }
        }
    }
}

sealed class GameEvent {
    object UserCreated : GameEvent()
    data class MissionCompleted(val score: Int) : GameEvent()
    data class Error(val message: String) : GameEvent()
}

data class GameUiState(
    val currentUser: User? = null,
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val currentCategoryMissions: List<Mission> = emptyList(),
    val unlockedMissions: List<Mission> = emptyList(),
    val currentMission: Mission? = null,
    val completedMissions: Int = 0,
    val totalScore: Int = 0
)

class GameViewModelFactory(private val repository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 