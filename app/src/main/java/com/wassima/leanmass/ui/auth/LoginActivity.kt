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
class LoginActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    // Vues — SANS ViewBinding
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialiser les vues
        etEmail       = findViewById(R.id.etEmail)
        etPassword    = findViewById(R.id.etPassword)
        btnLogin      = findViewById(R.id.btnLogin)
        btnGoRegister = findViewById(R.id.btnGoRegister)
        progressBar   = findViewById(R.id.progressBar)
        tvError       = findViewById(R.id.tvError)

        // Si déjà connecté → aller directement au calculateur
        if (viewModel.currentUser.value != null) {
            goToCalculator()
            return
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnLogin.isEnabled     = !loading
        }

        viewModel.currentUser.observe(this) { user ->
            if (user != null) goToCalculator()
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                // Toast visible immédiatement
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                // TextView rouge sous les boutons
                tvError.text       = error
                tvError.visibility = View.VISIBLE
                viewModel.clearError()
            } else {
                tvError.visibility = View.GONE
            }
        }
    }

    private fun goToCalculator() {
        startActivity(Intent(this, CalculatorActivity::class.java))
        finish()
    }
}