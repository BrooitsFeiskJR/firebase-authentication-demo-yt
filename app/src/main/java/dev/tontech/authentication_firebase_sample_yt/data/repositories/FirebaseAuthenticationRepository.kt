package dev.tontech.authentication_firebase_sample_yt.data.repositories

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dev.tontech.authentication_firebase_sample_yt.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticationRepository(private val auth: FirebaseAuth, context: Context) {
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?>
        get() = _user.asStateFlow()

    init {
        _user.value = auth.currentUser
    }

    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    private val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()).setAutoSelectEnabled(true).build()

    fun getSignResultFromIntent(intent: Intent?): String? {
        return oneTapClient.getSignInCredentialFromIntent(intent).googleIdToken
    }

    fun signInWithTokenCredential(idToken: String, accessToken: String?): Task<AuthResult> {
        val firebaseAuthCredential = GoogleAuthProvider.getCredential(idToken, accessToken)
        return auth.signInWithCredential(firebaseAuthCredential)
    }

    fun beginSignInWithGoogle(): Task<BeginSignInResult> {
        return oneTapClient.beginSignIn(signInRequest)
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

}