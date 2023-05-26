package dev.tontech.authentication_firebase_sample_yt.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.tontech.authentication_firebase_sample_yt.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var binding: FragmentRegisterBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding?.btnRegister?.setOnClickListener {
            val email: String = binding?.etEmail?.text.toString()
            val password: String = binding?.etPassword?.text.toString()
            val confirmPassword: String = binding?.etConfirmPassword?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    createUserWithEmailAndPassword(email, password)
                } else {
                    Toast.makeText(requireContext(), "Senha incompativel", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha os campos.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.d(TAG, "createUserWithEmailAndPassword:Success")
                Toast.makeText(requireContext(), "Conta criada com sucesso", Toast.LENGTH_SHORT).show()
                // val user = auth.currentUser
            } else {
                Log.w(TAG, "createUserWithEmailAndPassword:Failure", task.exception)
                Toast.makeText(requireContext(), "Authentication Failure", Toast.LENGTH_SHORT).show()
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