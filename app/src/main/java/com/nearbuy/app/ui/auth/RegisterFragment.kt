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
import com.nearbuy.app.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.authResult.observe(viewLifecycleOwner) { result ->
            binding.btnRegister.isEnabled = true
            when (result) {
                is AuthResult.Success -> {
                    findNavController().navigate(R.id.nav_home)
                }
                is AuthResult.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val name     = binding.etName.text.toString().trim()
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            binding.tilName.error     = null
            binding.tilEmail.error    = null
            binding.tilPassword.error = null
            if (name.isBlank()) {
                binding.tilName.error = "Name is required"
                return@setOnClickListener
            }
            if (email.isBlank()) {
                binding.tilEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.tilPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }
            binding.btnRegister.isEnabled = false
            authViewModel.register(name, email, password, password)
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.nav_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
