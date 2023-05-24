package dev.tontech.authentication_firebase_sample_yt

import android.content.Intent
import android.content.IntentSender
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val REQ_ONE_TAP = 2

    private lateinit var auth: FirebaseAuth
    private var binding: ActivityMainBinding? = null

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken

                when {
                    idToken != null -> {
                        Log.d(TAG, "Got ID token.")
                        val firebaseAuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseAuthCredential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithCredential:Success")
                            } else {
                                Log.w(TAG, "signInWithCredential:Failure", task.exception)
                            }
                        }
                    }  else -> {
                        Log.d(TAG, "No id token!")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .build()).setAutoSelectEnabled(true).build()

        binding?.btnLogin?.setOnClickListener {
            val email: String = binding?.etEmail?.text.toString()
            val password: String = binding?.etPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this@MainActivity, "Por favor, preencha os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.tvCreateAccount?.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding?.btnRegisterWithGoogle?.setOnClickListener {
            showAuthGoogle()
        }
    }

    private fun showAuthGoogle() {
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener { result ->
            try {
                startIntentSenderForResult(result.pendingIntent.intentSender, REQ_ONE_TAP, null, 0, 0, 0, null)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }.addOnFailureListener(this) { e ->
            e.localizedMessage?.let { e.localizedMessage?.let { it1 -> Log.d(TAG, it1) } }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.d(TAG, "signInUserWithEmailAndPassword:Success")
                Toast.makeText(this@MainActivity, "Login feito com sucesso", Toast.LENGTH_SHORT).show()
                // val user = auth.currentUser
            } else {
                Log.w(TAG, "signInUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(baseContext, "Authentication Failure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private var TAG  = "EmailAndPassword"
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}