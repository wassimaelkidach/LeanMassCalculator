package com.wassima.leanmass.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.wassima.leanmass.R
import java.util.Locale

class ResultFragment : Fragment() {

    private val viewModel: LBMViewModel by activityViewModels()

    private lateinit var ivResultIcon: ImageView
    private lateinit var tvLBMValue: TextView
    private lateinit var tvResultLabel: TextView
    private lateinit var tvResultDetails: TextView
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivResultIcon    = view.findViewById(R.id.ivResultIcon)
        tvLBMValue      = view.findViewById(R.id.tvLBMValue)
        tvResultLabel   = view.findViewById(R.id.tvResultLabel)
        tvResultDetails = view.findViewById(R.id.tvResultDetails)
        btnSave         = view.findViewById(R.id.btnSave)

        observeViewModel()

        btnSave.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
                ?: return@setOnClickListener
            viewModel.saveRecord(uid)
        }
    }

    private fun observeViewModel() {
        viewModel.currentRecord.observe(viewLifecycleOwner) { record ->
            if (record != null) {
                tvLBMValue.text = String.format(
                    Locale.getDefault(), "%.2f kg", record.lbmResult
                )

                val genderLabel = if (record.gender == "male") "Homme" else "Femme"
                tvResultDetails.text = String.format(
                    "%s · %.1f kg · %.1f cm",
                    genderLabel, record.weight, record.height
                )

                if (record.isSatisfactory) {
                    ivResultIcon.setImageResource(android.R.drawable.presence_online)
                    tvResultLabel.text = getString(R.string.result_satisfactory)
                    tvResultLabel.setTextColor(
                        requireContext().getColor(android.R.color.holo_green_dark)
                    )
                } else {
                    ivResultIcon.setImageResource(android.R.drawable.presence_away)
                    tvResultLabel.text = getString(R.string.result_unsatisfactory)
                    tvResultLabel.setTextColor(
                        requireContext().getColor(android.R.color.holo_orange_dark)
                    )
                }
            }
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { success ->
            when (success) {
                true -> {
                    Toast.makeText(requireContext(),
                        "Calcul sauvegardé !", Toast.LENGTH_SHORT).show()
                    viewModel.clearStatus()
                }
                false -> {
                    Toast.makeText(requireContext(),
                        "Erreur sauvegarde", Toast.LENGTH_SHORT).show()
                    viewModel.clearStatus()
                }
                null -> {}
            }
        }
    }
}