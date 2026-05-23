package com.wassima.leanmass.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wassima.leanmass.databinding.ActivityRegisterBinding
import com.wassima.leanmass.ui.calculator.CalculatorActivityVB

class RegisterActivityVB : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.register(email, password)
        }

        binding.btnGoLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled  = !loading
        }

        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                startActivity(Intent(this, CalculatorActivityVB::class.java))
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                binding.tvError.text       = error
                binding.tvError.visibility = View.VISIBLE
                viewModel.clearError()
            } else {
                binding.tvError.visibility = View.GONE
            }
        }
    }
}