package com.wassima.leanmass.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wassima.leanmass.databinding.ActivityLoginBinding
import com.wassima.leanmass.ui.calculator.CalculatorActivity

/**
 * Variante AVEC ViewBinding
 * Différence principale : plus de findViewById()
 * binding.etEmail au lieu de findViewById(R.id.etEmail)
 */
class LoginActivityVB : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    // ViewBinding — remplace tous les findViewById()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflation via ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.currentUser.value != null) {
            goToCalculator()
            return
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivityVB::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled     = !loading
        }

        viewModel.currentUser.observe(this) { user ->
            if (user != null) goToCalculator()
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

    private fun goToCalculator() {
        startActivity(Intent(this, CalculatorActivityVB::class.java))
        finish()
    }
}