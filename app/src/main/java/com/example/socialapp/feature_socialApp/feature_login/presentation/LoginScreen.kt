package com.example.socialapp.feature_socialApp.feature_login.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.socialapp.MainActivity
import com.example.socialapp.R
import com.example.socialapp.databinding.LoginBinding
import com.example.socialapp.feature_socialApp.feature_completeProfile.presentation.CompleteProfileScreen
import com.example.socialapp.feature_socialApp.feature_create_acct.presentation.CreateAcctScreen
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginScreen : AppCompatActivity() {


    private lateinit var binding: LoginBinding
    private val viewModel: LoginViewModel by viewModels()
    var googleToken: String? = null


    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val googleIntentAsyncResult = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = googleIntentAsyncResult.getResult(ApiException::class.java)
                googleToken = account.idToken
                viewModel.onEvents(LoginEvents.SignInWithGoogleClicked(googleToken))


            } catch (e: GoogleAuthException) {

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value.userLoggedIn == null
        }

        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        supportActionBar?.hide()



        binding.tvCreateAnAcct.setOnClickListener {
            viewModel.onEvents(LoginEvents.OneTimeEvents.CreateAcct)
        }

        binding.etEmail1.addTextChangedListener { emailText ->
            viewModel.onEvents(LoginEvents.UserEmail(emailText.toString()))
        }
        binding.etPassword1.addTextChangedListener { passwordText ->
            viewModel.onEvents(LoginEvents.UserPassword(passwordText.toString()))
        }

        binding.btnLogin.setOnClickListener {
            viewModel.onEvents(
                LoginEvents.LoginClicked(
                    viewModel.state.value.email,
                    viewModel.state.value.password
                )
            )
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            // This is the custom reuest from app to google
            // It includes the googleToken
            // It includes the email of user (where an email dialog containing various emails pops out and then the user an pik any of his google email to login with)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)

        }





        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        if (binding.etEmail1.text.toString() != state.email) {
                            binding.etEmail1.setText(state.email)
                        }
                        if (binding.etPassword1.text.toString() != state.password) {
                            binding.etPassword1.setText(state.password)
                        }
                        if (state.userLoggedIn == true) {
                            navigateToMain()
                        }
                    }
                }
                launch {
                    viewModel.event.collect { oneTimeEvents ->
                        when (oneTimeEvents) {
                            is LoginEvents.OneTimeEvents.ShowToast -> {
                                Toast.makeText(
                                    this@LoginScreen,
                                    oneTimeEvents.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is LoginEvents.OneTimeEvents.ShowError -> {
                                Toast.makeText(
                                    this@LoginScreen,
                                    oneTimeEvents.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is LoginEvents.OneTimeEvents.NavigateToCompleteProfileScreenUsingGoogleSIgnIn -> {

                                val googleUriToUrl = oneTimeEvents.uri.toString()
                                val intent =
                                    Intent(this@LoginScreen, CompleteProfileScreen::class.java)
                                intent.putExtra("googleProfilePicture", googleUriToUrl)
                                startActivity(intent)
                            }

                            is LoginEvents.OneTimeEvents.NavigateToMain -> {
                                val intent =
                                    Intent(this@LoginScreen, MainActivity::class.java)
                                startActivity(intent)
                            }

                            is LoginEvents.OneTimeEvents.CreateAcct -> {
                                val intent = Intent(this@LoginScreen, CreateAcctScreen::class.java)
                                startActivity(intent)

                            }

                            else -> {}

                        }
                    }

                }
            }
        }


    }

    private fun navigateToMain() {
        startActivity(
            Intent(this, MainActivity::class.java)
        )
        finish()
    }

}
