package com.wassima.leanmass.ui.history

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.wassima.leanmass.R

class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerHistory)
        tvEmpty      = findViewById(R.id.tvEmpty)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        adapter = HistoryAdapter(emptyList()) { record ->
            viewModel.deleteLocalRecord(record)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observer l'historique local (LiveData Room)
        viewModel.getLocalHistory(uid).observe(this) { records ->
            if (records.isEmpty()) {
                tvEmpty.visibility      = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmpty.visibility      = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateData(records)
            }
        }
    }
}