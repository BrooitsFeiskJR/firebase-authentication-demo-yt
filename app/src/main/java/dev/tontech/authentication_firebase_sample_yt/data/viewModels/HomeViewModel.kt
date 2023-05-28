package dev.tontech.authentication_firebase_sample_yt.data.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.data.model.Employee
import dev.tontech.authentication_firebase_sample_yt.data.model.UserUiState
import dev.tontech.authentication_firebase_sample_yt.data.repositories.FirebaseAuthenticationRepository
import dev.tontech.authentication_firebase_sample_yt.data.repositories.FirebaseRealTimeDatabaseRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: FirebaseAuthenticationRepository,
    private val databaseRepository: FirebaseRealTimeDatabaseRepository,
    ): ViewModel() {
    init {
        viewModelScope.launch {
            readUser()
            readDatabase()
        }
    }

    private val _user = MutableLiveData<UserUiState>()
    val user: LiveData<UserUiState>
        get() = _user

    private val _employee = MutableLiveData<Employee>()
    val employee: LiveData<Employee>
        get() = _employee

    fun writeMessageDatabase(message: Employee) {
        viewModelScope.launch {
            databaseRepository.writeMessageIntoDatabase(message)
        }
    }

    private fun readUser() {
        viewModelScope.launch {
            authRepository.user.collect { u ->
                if (u != null) {
                    _user.value = UserUiState(user = u, isSignIn = true)
                }
            }
        }
    }

    private fun readDatabase() {
        viewModelScope.launch {
            databaseRepository.readEmployeeIntoDatabase()
            databaseRepository.employee.collect {emp ->
                if(emp != null) {
                    _employee.value = emp
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return HomeViewModel(
                    FirebaseAuthenticationRepository(auth = Firebase.auth, context = application),
                    FirebaseRealTimeDatabaseRepository(Firebase.database)
                ) as T
            }
        }
    }
}