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
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.data.enums.LoginUiState
import dev.tontech.authentication_firebase_sample_yt.data.repositories.FirebaseAuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val application: Application,
    private val repository: FirebaseAuthenticationRepository)
    : AndroidViewModel(application) {

    private val _loginUiState = MutableStateFlow(LoginUiState.LOADING)
    val loginUiState: StateFlow<LoginUiState>
        get() = _loginUiState.asStateFlow()

    fun getSignResultFromIntent(intent: Intent?) {
        viewModelScope.launch {
            try {
                val idToken = repository.getSignResultFromIntent(intent)
                when {
                    idToken != null -> {
                        Log.d(TAG, "Got ID token.")
                        repository.signInWithTokenCredential(idToken, null).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                _loginUiState.value = LoginUiState.SUCCESS
                            } else {
                                _loginUiState.value = LoginUiState.ERROR
                                Log.d(TAG, "ERROR FROM: getSignResultFromIntent")
                            }
                        }
                    }  else -> Log.d(TAG, "No id token!")
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.d(TAG, "One-tap dialog was closed")
                        _loginUiState.value = LoginUiState.ERROR
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        Log.d(TAG, "One-tap encountered a network error.")
                        _loginUiState.value = LoginUiState.ERROR
                    }
                    else -> {
                        Log.d(TAG, "Couldn't get credential from result." +
                                " (${e.localizedMessage})")
                        _loginUiState.value = LoginUiState.ERROR
                    }
                }
            }
        }
    }

    fun signInWithGoogle(): Task<BeginSignInResult> {
        return repository.beginSignInWithGoogle()
    }

     fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            repository.loginWithEmailAndPassword(email, password)
            _loginUiState.value = LoginUiState.SUCCESS
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            repository.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _loginUiState.value = LoginUiState.SUCCESS
                } else {
                    _loginUiState.value = LoginUiState.ERROR
                }
            }
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
                    application = application,
                    repository = FirebaseAuthenticationRepository(auth = firebaseAuth, context = application)
                ) as T
            }
        }
    }
}