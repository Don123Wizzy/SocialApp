package com.example.socialapp.feature_socialApp.feature_login.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.feature_socialApp.feature_login.domain.use_cases.CheckAuthStateUseCase
import com.example.socialapp.feature_socialApp.feature_login.domain.use_cases.CreateAcct
import com.example.socialapp.feature_socialApp.feature_login.domain.use_cases.ForgotPassword
import com.example.socialapp.feature_socialApp.feature_login.domain.use_cases.Login
import com.example.socialapp.feature_socialApp.feature_login.domain.use_cases.LoginUseCase
import com.example.socialapp.feature_socialApp.feature_login.domain.use_cases.SignInWithGoogle
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private var login: Login,
    private var createAcct: CreateAcct,
    private var forgotPassword: ForgotPassword,
    private var signInWithGoogle: SignInWithGoogle,
    private val checkAuthStateUseCase: CheckAuthStateUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    init {
        checkUserLoggedInState()
    }


    val loginUseCase = LoginUseCase(login, createAcct, forgotPassword, signInWithGoogle)

    private val _event = Channel<LoginEvents>()
    val event = _event.receiveAsFlow()

    private fun sendOneTimeEvents(event: LoginEvents) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onEvents(loginEvents: LoginEvents) {
        when (loginEvents) {
            is LoginEvents.UserEmail -> {
                _state.value = _state.value.copy(
                    email = loginEvents.email,
                )
            }

            is LoginEvents.UserPassword -> {
                _state.value = _state.value.copy(
                    password = loginEvents.password
                )
            }

            is LoginEvents.LoginClicked -> {
                viewModelScope.launch {
                    val loginResult = loginUseCase.login(loginEvents.email, loginEvents.password)
                    loginResult.fold(
                        onSuccess = { loginResult ->
                            _state.value = _state.value.copy(
                                userName = loginResult.userName
                            )
                            sendOneTimeEvents(LoginEvents.OneTimeEvents.ShowToast("Welcome! ${loginResult.userName}"))

                            sendOneTimeEvents(LoginEvents.OneTimeEvents.NavigateToMain)

                        },
                        onFailure = { error ->
                            sendOneTimeEvents(LoginEvents.OneTimeEvents.ShowError("Something went wrong\n" + "Couldn't sign in user"))


                        }
                    )
                }
            }

            is LoginEvents.ForgotPasswordClicked -> {

            }

            is LoginEvents.SignInWithGoogleClicked -> {
                viewModelScope.launch {

                    try {
                        val credentialObtainedAfterGivingGoogleItToken =
                            loginUseCase.signInWithGoogle(loginEvents.googleToken)

                        credentialObtainedAfterGivingGoogleItToken.fold(
                            onSuccess = { firebaseCredential ->
                                //sendOneTimeEvents(LoginEvents.OneTimeEvents.ShowToast("Welcome! ${firebaseCredential.googleUserName}"))
                                sendOneTimeEvents(
                                    LoginEvents.OneTimeEvents.NavigateToCompleteProfileScreenUsingGoogleSIgnIn(
                                        firebaseCredential.googleProfileUrl
                                    )
                                )
                            },
                            onFailure = { error -> // This error type here is a throwable ( onFailure: (Throwable) -> R), although the actual input type i passed in the logic repository was an exception but since an exception is a child of the throwable, it also works
                                val message = when (error) {
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        "Your Google sign-in credentials are invalid."
                                    }

                                    is FirebaseAuthException -> {
                                        "We couldn't find your account."

                                    }

                                    else -> {
                                        "Something went wrong. Please try again."
                                    }
                                }
                                sendOneTimeEvents(
                                    LoginEvents.OneTimeEvents.ShowError(message)
                                )
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("GOOGLE_SIGN_IN", "Something went wrong!!", e)
                    }


                }
            }

            is LoginEvents.OneTimeEvents.CreateAcct -> {
                sendOneTimeEvents(LoginEvents.OneTimeEvents.CreateAcct)
            }

            else -> {}

        }

    }

    private fun checkUserLoggedInState() {

        val result = checkAuthStateUseCase()

        _state.value = _state.value.copy(
            userLoggedIn = result
        )
    }


}