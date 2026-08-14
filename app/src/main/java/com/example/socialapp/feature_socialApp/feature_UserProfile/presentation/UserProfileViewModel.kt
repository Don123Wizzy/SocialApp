package com.example.socialapp.feature_socialApp.feature_UserProfile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.error.UserProfileError
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.error.UserProfileException
import com.example.socialapp.feature_socialApp.feature_UserProfile.domain.use_case.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    init {
        getUserProfile()
    }

    private val _events = Channel<UserProfileEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private fun sendOneTimeEvents(events: UserProfileEvents) {
        viewModelScope.launch {
            _events.send(events)
        }
    }

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState = _uiState.asStateFlow()

    fun onEvents(events: UserProfileEvents) {
        when (events) {
            is UserProfileEvents.EditUserVisualDetailsButton -> {
                sendOneTimeEvents(UserProfileEvents.EditUserVisualDetailsButton)

            }

            is UserProfileEvents.EditUserWrittenDetailsButton -> {
                sendOneTimeEvents(UserProfileEvents.EditUserWrittenDetailsButton)
            }
            is UserProfileEvents.StartEditActivity -> {
                sendOneTimeEvents(UserProfileEvents.StartEditActivity)
            }
            else -> {}

        }
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            val result = getProfileUseCase()
            result.fold(
                onSuccess = { userProfileData ->
                    _uiState.value = _uiState.value.copy(
                        profileImage = userProfileData.userProfileImage,
                        userName = userProfileData.userName,
                        jobTitle = userProfileData.jobTitle,
                        bio = userProfileData.bio
                    )

                },
                onFailure = { exception ->
                    when(exception){
                        is UserProfileException ->{
                            when(exception.error){
                                is UserProfileError.FetchProfileFailed -> {
                                    sendOneTimeEvents(UserProfileEvents.FetchProfileFailedError("We couldn't load your profile. Please check your internet connection and try again."))
                                }
                            }
                        }

                    }


                }
            )
        }
    }
}