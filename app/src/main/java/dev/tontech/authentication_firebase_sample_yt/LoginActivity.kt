package dev.tontech.authentication_firebase_sample_yt

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.data.LoginViewModel
import dev.tontech.authentication_firebase_sample_yt.data.enums.LoginUiState
import dev.tontech.authentication_firebase_sample_yt.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val reqOneTap = 2

    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    private lateinit var auth: FirebaseAuth
    private var binding: ActivityLoginBinding? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            reqOneTap -> {
                viewModel.getSignResultFromIntent(data)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth

        lifecycleScope.launch {
            viewModel.loginUiState.collect {state ->
                when(state) {
                    LoginUiState.SUCCESS -> {
                        Log.d(TAG, "Success")
                    }
                    LoginUiState.LOADING -> {
                        Log.d(TAG, "Loading")
                    }
                    LoginUiState.ERROR -> {
                        Log.d(TAG, "Error")
                    }
                }
            }
        }

        binding?.btnLogin?.setOnClickListener {
            val email: String = binding?.etEmail?.text.toString()
            val password: String = binding?.etPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.signInWithEmailAndPassword(email, password)
                    viewModel.user.collect {user ->
                        if (user != null) {
                            Log.d(TAG, user.displayName.toString())
                        }
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Por favor, preencha os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnRegisterWithGoogle?.setOnClickListener {
            showAuthGoogle()
        }

        binding?.tvCreateAccount?.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAuthGoogle() {
        viewModel.oneTapClient.beginSignIn(viewModel.signInRequest).addOnSuccessListener { result ->
            try {
                startIntentSenderForResult(result.pendingIntent.intentSender, reqOneTap, null, 0, 0, 0, null)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }.addOnFailureListener(this) { e ->
            e.localizedMessage?.let { e.localizedMessage?.let { it1 -> Log.d(TAG, it1) } }
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