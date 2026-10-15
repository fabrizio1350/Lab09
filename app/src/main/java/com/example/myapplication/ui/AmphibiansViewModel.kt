package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.AmphibiansApplication
import com.example.myapplication.data.AmphibiansRepository
import com.example.myapplication.model.Amphibian
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface AmphibiansUiState {
    data class Success(val amphibians: List<Amphibian>) : AmphibiansUiState
    object Error : AmphibiansUiState
    object Loading : AmphibiansUiState
}

class AmphibiansViewModel(private val amphibiansRepository: AmphibiansRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AmphibiansUiState>(AmphibiansUiState.Loading)
    val uiState: StateFlow<AmphibiansUiState> = _uiState.asStateFlow()

    init {
        getAmphibians()
    }

    fun getAmphibians() {
        viewModelScope.launch {
            _uiState.value = AmphibiansUiState.Loading
            try {
                val listResult = amphibiansRepository.getAmphibians()
                _uiState.value = AmphibiansUiState.Success(listResult)
            } catch (e: IOException) {
                _uiState.value = AmphibiansUiState.Error
            } catch (e: retrofit2.HttpException) {
                _uiState.value = AmphibiansUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AmphibiansApplication)
                val amphibiansRepository = application.container.amphibiansRepository
                AmphibiansViewModel(amphibiansRepository)
            }
        }
    }
}