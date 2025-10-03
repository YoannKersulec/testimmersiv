package com.example.testimmersiv.ui.theme.view

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testimmersiv.domain.GetVidsUsecase
import com.example.testimmersiv.domain.VideoSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = false,
    val vids: List<VideoSample> = emptyList(),
    val error: String? = null
)

class MainViewModel(
    private val getVidsUsecase: GetVidsUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state

    fun loadVids() {
        Log.i("MainVM", "Load VIDS")
        viewModelScope.launch {
            _state.value = MainUiState(isLoading = true)
            try {
                val vids = getVidsUsecase()
                Log.i("MainVM", "Data $vids")
                _state.value = MainUiState(vids = vids)
            } catch (e: Exception) {
                Log.e("MainVM", "Error ${e.message}")
                _state.value = MainUiState(error = e.message)
            }
        }
    }
}