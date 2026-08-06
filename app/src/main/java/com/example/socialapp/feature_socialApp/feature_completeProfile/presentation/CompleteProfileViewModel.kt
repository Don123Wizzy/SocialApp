package com.example.socialapp.feature_socialApp.feature_completeProfile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.use_case.CompleteProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    val completeProfileUseCase : CompleteProfileUseCase
) : ViewModel() {

    private val _oneTimeEvents = Channel<CompleteProfileEvents>(Channel.BUFFERED)
    val oneTimeEvents = _oneTimeEvents.receiveAsFlow()

    private fun sendOneTimeEvents(events: CompleteProfileEvents) {
        viewModelScope.launch {
            _oneTimeEvents.send(events)
        }

    }

    private val _uiState = MutableStateFlow(CompleteProfileState())
    val uiState = _uiState.asStateFlow()

    fun onEvents (completeProfileEvents: CompleteProfileEvents){
        when (completeProfileEvents){
            is CompleteProfileEvents.JobTitle -> {
                _uiState.value = _uiState.value.copy(
                    jobTitle = completeProfileEvents.jobTitle
                )

            }
            is CompleteProfileEvents.Bio -> {
                _uiState.value = _uiState.value.copy(
                    bio = completeProfileEvents.bio
                )

            }
            is CompleteProfileEvents.Continue -> {
                val jobTitle = _uiState.value.jobTitle
                val bio = _uiState.value.bio
                val selectedUri = _uiState.value.uri
                viewModelScope.launch {
                    selectedUri?.let {
                        completeProfileUseCase(jobTitle,bio, it)
                    }
                    sendOneTimeEvents(CompleteProfileEvents.NavigateToMain)
                }


            }
            is CompleteProfileEvents.ProfileImageSelected -> {
                _uiState.value = _uiState.value.copy(
                    uri = completeProfileEvents.uri
                )

            }
            else -> {}
        }

    }


}