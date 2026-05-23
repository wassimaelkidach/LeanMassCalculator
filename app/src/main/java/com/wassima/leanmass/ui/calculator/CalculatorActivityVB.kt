package com.wassima.leanmass.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.wassima.leanmass.databinding.ActivityCalculatorBinding
import com.wassima.leanmass.ui.history.HistoryActivityVB
import java.util.Locale

class CalculatorActivityVB : AppCompatActivity() {

    private val viewModel: LBMViewModel by viewModels()
    private lateinit var binding: ActivityCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnCalculate.setOnClickListener {
            val weight = binding.etWeight.text.toString().toDoubleOrNull()
            val height = binding.etHeight.text.toString().toDoubleOrNull()

            if (weight == null || height == null || weight <= 0 || height <= 0) {
                Toast.makeText(this,
                    "Veuillez saisir des valeurs valides",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = if (binding.rgGender.checkedRadioButtonId == R.id.rbMale)
                "male" else "female"
            viewModel.calculate(weight, height, gender)
        }

        binding.btnSave.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            viewModel.saveRecord(uid)
        }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivityVB::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.currentRecord.observe(this) { record ->
            if (record != null) {
                binding.cardResult.visibility = View.VISIBLE
                binding.tvLBMValue.text = String.format(
                    Locale.getDefault(), "%.2f kg", record.lbmResult
                )

                if (record.isSatisfactory) {
                    binding.ivResultIcon.setImageResource(android.R.drawable.presence_online)
                    binding.tvResultLabel.text = "Résultat satisfaisant ✓"
                    binding.tvResultLabel.setTextColor(
                        getColor(android.R.color.holo_green_dark))
                } else {
                    binding.ivResultIcon.setImageResource(android.R.drawable.presence_away)
                    binding.tvResultLabel.text = "Résultat à surveiller ⚠"
                    binding.tvResultLabel.setTextColor(
                        getColor(android.R.color.holo_orange_dark))
                }
            }
        }

        viewModel.saveStatus.observe(this) { success ->
            when (success) {
                true  -> {
                    Toast.makeText(this, "✓ Sauvegardé !", Toast.LENGTH_SHORT).show()
                    viewModel.clearStatus()
                }
                false -> {
                    Toast.makeText(this, "Erreur sauvegarde", Toast.LENGTH_SHORT).show()
                    viewModel.clearStatus()
                }
                null -> {}
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