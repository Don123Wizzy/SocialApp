package com.example.socialapp.feature_socialApp.feature_completeProfile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.error.CompleteProfileError
import com.example.socialapp.feature_socialApp.feature_completeProfile.domain.error.CompleteProfileErrorException
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
    val completeProfileUseCase: CompleteProfileUseCase
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

    fun onEvents(completeProfileEvents: CompleteProfileEvents) {
        when (completeProfileEvents) {
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

            is CompleteProfileEvents.NavigateToMain -> {

            }

            is CompleteProfileEvents.Continue -> {
                val jobTitle = _uiState.value.jobTitle
                val bio = _uiState.value.bio
                val selectedUri = _uiState.value.uri
                viewModelScope.launch {

                    val result = completeProfileUseCase(jobTitle, bio, selectedUri)
                    result.fold(
                        onSuccess = {
                            sendOneTimeEvents(CompleteProfileEvents.NavigateToMain)
                        },
                        onFailure = { exception ->
                            when (exception) {
                                is CompleteProfileErrorException -> {
                                    when (exception.error) { // at this point, exception is a type or an instance of the CompleteProfileErrorException, hence it an access the error property in the class
                                        is CompleteProfileError.CloudinaryUploadFailed -> {
                                            sendOneTimeEvents(
                                                CompleteProfileEvents.CloudinaryUploadFailedMessageToast(
                                                    "We couldn't upload your profile picture. Please check your internet connection and try again."
                                                )
                                            )
                                        }

                                        is CompleteProfileError.FireStoreUpdateFailed -> {
                                            sendOneTimeEvents(
                                                CompleteProfileEvents.FireStoreUpdateFailedMessageToast(
                                                    "We couldn't save your profile. Please try again."
                                                )
                                            )
                                        }

                                        is CompleteProfileError.UserNotLoggedIn -> {
                                            sendOneTimeEvents(
                                                CompleteProfileEvents.UserNotLoggedInMessageToast(
                                                    "Your session has expired. Please sign in again."
                                                )
                                            )
                                        }

                                    }
                                }

                            }

                        }
                    )

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