package com.wassima.leanmass.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.wassima.leanmass.R
import com.wassima.leanmass.ui.history.HistoryActivity
import java.util.Locale
import com.wassima.leanmass.ui.auth.LoginActivity

class CalculatorActivity : AppCompatActivity() {

    private val viewModel: LBMViewModel by viewModels()

    private lateinit var rgGender: RadioGroup
    private lateinit var etWeight: TextInputEditText
    private lateinit var etHeight: TextInputEditText
    private lateinit var btnCalculate: Button
    private lateinit var btnSave: Button
    private lateinit var btnHistory: Button
    private lateinit var cardResult: CardView
    private lateinit var ivResultIcon: ImageView
    private lateinit var tvLBMValue: TextView
    private lateinit var tvResultLabel: TextView

    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        rgGender    = findViewById(R.id.rgGender)
        etWeight    = findViewById(R.id.etWeight)
        etHeight    = findViewById(R.id.etHeight)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnSave     = findViewById(R.id.btnSave)
        btnHistory  = findViewById(R.id.btnHistory)
        cardResult  = findViewById(R.id.cardResult)
        ivResultIcon = findViewById(R.id.ivResultIcon)
        tvLBMValue  = findViewById(R.id.tvLBMValue)
        tvResultLabel = findViewById(R.id.tvResultLabel)
        btnLogout = findViewById(R.id.btnLogout)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        btnCalculate.setOnClickListener {
            val weight = etWeight.text.toString().toDoubleOrNull()
            val height = etHeight.text.toString().toDoubleOrNull()

            if (weight == null || height == null) {
                Toast.makeText(this, "Veuillez saisir un poids et une taille valides", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = if (rgGender.checkedRadioButtonId == R.id.rbMale) "male" else "female"
            viewModel.calculate(weight, height, gender)
        }

        btnSave.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            viewModel.saveRecord(uid)
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.currentRecord.observe(this) { record ->
            if (record != null) {
                cardResult.visibility = View.VISIBLE

                // Afficher la valeur LBM
                tvLBMValue.text = String.format(Locale.getDefault(), "%.2f kg", record.lbmResult)

                // Icône et message selon le résultat
                if (record.isSatisfactory) {
                    ivResultIcon.setImageResource(android.R.drawable.presence_online)
                    tvResultLabel.text      = "Résultat satisfaisant ✓"
                    tvResultLabel.setTextColor(getColor(android.R.color.holo_green_dark))
                } else {
                    ivResultIcon.setImageResource(android.R.drawable.presence_away)
                    tvResultLabel.text      = "Résultat à surveiller ⚠"
                    tvResultLabel.setTextColor(getColor(android.R.color.holo_orange_dark))
                }
            }
        }

        viewModel.saveStatus.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Calcul sauvegardé !", Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
            } else if (success == false) {
                Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                viewModel.clearStatus()
            }
        }
    }
}