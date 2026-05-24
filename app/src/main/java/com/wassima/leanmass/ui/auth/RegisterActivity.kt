package com.wassima.leanmass.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.wassima.leanmass.R
import com.wassima.leanmass.ui.calculator.CalculatorActivity
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etEmail      = findViewById(R.id.etEmail)
        etPassword   = findViewById(R.id.etPassword)
        btnRegister  = findViewById(R.id.btnRegister)
        btnGoLogin   = findViewById(R.id.btnGoLogin)
        progressBar  = findViewById(R.id.progressBar)
        tvError      = findViewById(R.id.tvError)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            viewModel.register(email, password)
        }

        btnGoLogin.setOnClickListener {
            finish() // retour à LoginActivity
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnRegister.isEnabled  = !loading
        }

        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                // Inscription réussie → aller au calculateur
                startActivity(Intent(this, CalculatorActivity::class.java))
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                tvError.text       = error
                tvError.visibility = View.VISIBLE
                viewModel.clearError()
            } else {
                tvError.visibility = View.GONE
            }
        }
    }
}