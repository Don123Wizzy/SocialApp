package com.example.socialapp.feature_socialApp.feature_profileFragment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.use_case.GetUserProfilePicAndUserNameUseCase
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    val getUserProfilePicAndUserNameUseCase: GetUserProfilePicAndUserNameUseCase
) : ViewModel() {
    init {
        getUserProfilePicAndUserName()
    }

    private val _events = Channel<ProfileFragmentEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private fun sendOneTimeEvents(events: ProfileFragmentEvents) {
        viewModelScope.launch {
            _events.send(events)
        }
    }

    private val _uiState = MutableStateFlow(ProfileFragmentState())
    val uiState = _uiState.asStateFlow()

    fun onEvents(events: ProfileFragmentEvents) {
        when (events) {
            is ProfileFragmentEvents.OpenProfileActivity -> {
                sendOneTimeEvents(ProfileFragmentEvents.OpenProfileActivity)
            }

            is ProfileFragmentEvents.OpenReportActivity -> {
                sendOneTimeEvents(ProfileFragmentEvents.OpenReportActivity)

            }

            is ProfileFragmentEvents.OpenSettingsActivity -> {
                sendOneTimeEvents(ProfileFragmentEvents.OpenSettingsActivity)
            }

            is ProfileFragmentEvents.OpenFriendListActivity -> {
                sendOneTimeEvents(ProfileFragmentEvents.OpenFriendListActivity)
            }

            is ProfileFragmentEvents.OpenNotificationActivity -> {
                sendOneTimeEvents(ProfileFragmentEvents.OpenNotificationActivity)
            }

            is ProfileFragmentEvents.Logout -> {
                sendOneTimeEvents(ProfileFragmentEvents.Logout)
            }
            else -> {}
        }

    }

    private fun getUserProfilePicAndUserName (){
        viewModelScope.launch {
            val result = getUserProfilePicAndUserNameUseCase()
            result.fold(
                onSuccess = { profileImageAndUserNameModel ->
                    val profilePicture = profileImageAndUserNameModel.profileImage
                    val userName = profileImageAndUserNameModel.userName
                    _uiState.value = _uiState.value.copy(
                        userProfilePicture = profilePicture,
                        userName = userName
                    )

                },
                onFailure = { error ->
                    val errorMessage = when(error){
                        is FirebaseFirestoreException -> {"Poor internet connection, please try again"}
                        else -> {"Could not load profile, please try again"}
                    }
                    sendOneTimeEvents(ProfileFragmentEvents.ErrorMessage(errorMessage))
                }
            )

        }
    }

}