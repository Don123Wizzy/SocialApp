package com.example.socialapp.feature_socialApp.feature_profileFragment.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.model.UserDetailError
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.model.UserDetailException
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.use_case.GetUserProfilePicAndUserNameUseCase
import com.example.socialapp.feature_socialApp.feature_profileFragment.domain.use_case.SignOutUseCase
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
    private val getUserProfilePicAndUserNameUseCase: GetUserProfilePicAndUserNameUseCase,
    private val signOutUseCase: SignOutUseCase
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
                signOutUseCase()
                sendOneTimeEvents(ProfileFragmentEvents.Logout)
            }
            else -> {}
        }

    }

    private fun getUserProfilePicAndUserName (){
        viewModelScope.launch {
            try {
                val result = getUserProfilePicAndUserNameUseCase()
                result.collect { collectedUserDocument ->
                    _uiState.value = _uiState.value.copy(
                        userProfilePicture = collectedUserDocument.userProfileImage
                    )
                    _uiState.value = _uiState.value.copy(
                        userName = collectedUserDocument.name
                    )
                }
            }catch (e : Exception){
                when (e){
                    is UserDetailException -> // checking whether the exception is my application error
                        when (e.error){  // if it is, which one of the children errors in the sealed interface it is??
                            is UserDetailError.FetchUserDetailsFailed -> {
                                sendOneTimeEvents(ProfileFragmentEvents.FetchProfileFailedError("We couldn't load your profile. Please check your internet connection"))
                            }
                            is UserDetailError.UserNotLoggedIn -> {
                                sendOneTimeEvents(ProfileFragmentEvents.UserNotLoggedInError("You’re not signed in. Please sign in and try again."))
                            }
                        }
                }

            }


        }
    }

}