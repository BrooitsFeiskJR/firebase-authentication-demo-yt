package dev.tontech.authentication_firebase_sample_yt.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.tontech.authentication_firebase_sample_yt.data.model.Employee
import dev.tontech.authentication_firebase_sample_yt.data.viewModels.HomeViewModel
import dev.tontech.authentication_firebase_sample_yt.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null

    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.writeMessageDatabase(Employee(
                    id = 1,
                    name = "Rafaela Pavan",
                    cpf = "10193471981",
                    birthdayDate = "21/02/2004",
                    email = "luizantonio0125@gmail.com"
                ))
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.employee.observe(viewLifecycleOwner) { emp ->
                    if (emp != null) {
                        binding?.tvUsername?.text = emp.cpf
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}