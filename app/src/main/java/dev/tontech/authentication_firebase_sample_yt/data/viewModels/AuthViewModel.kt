package dev.tontech.authentication_firebase_sample_yt.data.viewModels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.R
import dev.tontech.authentication_firebase_sample_yt.data.enums.LoginUiState
import dev.tontech.authentication_firebase_sample_yt.data.repositories.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth: FirebaseAuth,
    private val application: Application,
    private val repository: FirebaseRepository)
    : AndroidViewModel(application) {



    private val _loginUiState = MutableStateFlow(LoginUiState.LOADING)
    val loginUiState: StateFlow<LoginUiState>
        get() = _loginUiState.asStateFlow()


    val oneTapClient: SignInClient = Identity.getSignInClient(getApplication<Application>().applicationContext)

    val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
    .setGoogleIdTokenRequestOptions(
    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
    .setSupported(true)
    .setFilterByAuthorizedAccounts(false)
    .setServerClientId(application.getString(R.string.default_web_client_id))
    .build()).setAutoSelectEnabled(true).build()



    fun getSignResultFromIntent(intent: Intent?) {
        val idToken = oneTapClient.getSignInCredentialFromIntent(intent).googleIdToken

        when {
            idToken != null -> {
                Log.d(TAG, "Got ID token.")
                val firebaseAuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseAuthCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginUiState.value = LoginUiState.SUCCESS
                    } else {
                        _loginUiState.value = LoginUiState.ERROR
                        Log.d(TAG, "ERROR FROM: getSignResultFromIntent")
                    }
                }
            }  else -> {
            Log.d(TAG, "No id token!")
            }
        }
    }


     fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            repository.loginWithEmailAndPassword(email, password)
            _loginUiState.value = LoginUiState.SUCCESS
        }
    }

    companion object {
        private var TAG = "EmailAndPassword"

        // TODO: Make this dependency injection using Dagger or Koin
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val firebaseAuth = Firebase.auth

                return AuthViewModel(
                    auth = firebaseAuth,
                    application = application,
                    repository = FirebaseRepository(firebaseAuth)
                ) as T
            }
        }
    }
}