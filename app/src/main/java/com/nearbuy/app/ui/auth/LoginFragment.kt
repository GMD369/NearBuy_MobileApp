package com.nearbuy.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.authResult.observe(viewLifecycleOwner) { result ->
            binding.btnLogin.isEnabled = true
            when (result) {
                is AuthResult.Success -> {
                    findNavController().navigate(R.id.nav_home)
                }
                is AuthResult.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            binding.tilEmail.error    = null
            binding.tilPassword.error = null
            if (email.isBlank()) {
                binding.tilEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isBlank()) {
                binding.tilPassword.error = "Password is required"
                return@setOnClickListener
            }
            binding.btnLogin.isEnabled = false
            authViewModel.login(email, password)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.nav_register)
        }

        binding.btnSkipLogin.setOnClickListener {
            findNavController().navigate(R.id.nav_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
