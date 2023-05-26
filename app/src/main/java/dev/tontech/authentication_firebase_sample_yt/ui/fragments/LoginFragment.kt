package dev.tontech.authentication_firebase_sample_yt.ui.fragments

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.R
import dev.tontech.authentication_firebase_sample_yt.data.enums.LoginUiState
import dev.tontech.authentication_firebase_sample_yt.data.viewModels.AuthViewModel
import dev.tontech.authentication_firebase_sample_yt.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private val reqOneTap = 2

    private val viewModel: AuthViewModel by viewModels { AuthViewModel.Factory }

    private lateinit var auth: FirebaseAuth
    private var binding: FragmentLoginBinding? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            reqOneTap -> {
                viewModel.getSignResultFromIntent(data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = view.findNavController()

        auth = Firebase.auth

        lifecycleScope.launch {
            viewModel.loginUiState.collect {state ->
                when(state) {
                    LoginUiState.SUCCESS -> {
                        navController.navigate(R.id.action_loginFragment_to_homeFragment)
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
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnRegisterWithGoogle?.setOnClickListener {
            showAuthGoogle()
        }

        binding?.tvCreateAccount?.setOnClickListener {
           navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun showAuthGoogle() {
        viewModel.signInWithGoogle().addOnSuccessListener { result ->
            try {
                startIntentSenderForResult(result.pendingIntent.intentSender, reqOneTap, null, 0, 0, 0, null)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }.addOnFailureListener(requireActivity()) { e ->
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