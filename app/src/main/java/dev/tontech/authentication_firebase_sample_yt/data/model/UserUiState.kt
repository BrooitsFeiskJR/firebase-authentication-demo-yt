package dev.tontech.authentication_firebase_sample_yt.data.model

import com.google.firebase.auth.FirebaseUser

data class UserUiState(
    val user: FirebaseUser,
    val isSignIn: Boolean = false
)