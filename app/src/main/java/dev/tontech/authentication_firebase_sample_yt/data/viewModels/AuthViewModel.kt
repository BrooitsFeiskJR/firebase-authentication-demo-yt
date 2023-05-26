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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.data.enums.LoginUiState
import dev.tontech.authentication_firebase_sample_yt.data.repositories.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val application: Application,
    private val repository: FirebaseRepository)
    : AndroidViewModel(application) {

    private val _loginUiState = MutableStateFlow(LoginUiState.LOADING)
    val loginUiState: StateFlow<LoginUiState>
        get() = _loginUiState.asStateFlow()

    fun getSignResultFromIntent(intent: Intent?) {
        viewModelScope.launch {
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
                }  else -> {
                Log.d(TAG, "No id token!")
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
                    repository = FirebaseRepository(firebaseAuth, application)
                ) as T
            }
        }
    }
}