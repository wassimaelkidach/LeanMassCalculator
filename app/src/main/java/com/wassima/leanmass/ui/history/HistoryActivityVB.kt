package com.wassima.leanmass.ui.history

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.wassima.leanmass.databinding.ActivityHistoryBinding

class HistoryActivityVB : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        adapter = HistoryAdapter(emptyList()) { record ->
            viewModel.deleteLocalRecord(record)
        }

        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = adapter

        viewModel.getLocalHistory(uid).observe(this) { records ->
            if (records.isEmpty()) {
                binding.tvEmpty.visibility      = View.VISIBLE
                binding.recyclerHistory.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility      = View.GONE
                binding.recyclerHistory.visibility = View.VISIBLE
                adapter.updateData(records)
            }
        }
    }
}