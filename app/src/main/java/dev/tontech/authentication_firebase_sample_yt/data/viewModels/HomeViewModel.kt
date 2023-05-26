package dev.tontech.authentication_firebase_sample_yt.data.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.data.repositories.FirebaseRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: FirebaseRepository): ViewModel() {

    init {
        viewModelScope.launch {
            repository.user.collect { user ->
                _user.value = user
            }
        }
    }

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser?>
        get() = _user

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return HomeViewModel(FirebaseRepository(Firebase.auth)) as T
            }
        }
    }
}