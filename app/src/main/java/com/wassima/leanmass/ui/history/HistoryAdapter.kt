package com.wassima.leanmass.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wassima.leanmass.R
import com.wassima.leanmass.data.local.entity.LBMRecordEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var records: List<LBMRecordEntity>,
    private val onDelete: (LBMRecordEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView     = itemView.findViewById(R.id.ivItemIcon)
        val tvLBM: TextView       = itemView.findViewById(R.id.tvItemLBM)
        val tvDetails: TextView   = itemView.findViewById(R.id.tvItemDetails)
        val tvDate: TextView      = itemView.findViewById(R.id.tvItemDate)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        // Valeur LBM
        holder.tvLBM.text = String.format(Locale.getDefault(), "LBM : %.2f kg", record.lbmResult)

        // Détails
        val genderLabel = if (record.gender == "male") "Homme" else "Femme"
        holder.tvDetails.text = "$genderLabel · ${record.weight} kg · ${record.height} cm"

        // Date formatée
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(record.timestamp))

        // Icône satisfaisant / à surveiller
        if (record.isSatisfactory) {
            holder.ivIcon.setImageResource(android.R.drawable.presence_online)
        } else {
            holder.ivIcon.setImageResource(android.R.drawable.presence_away)
        }

        // Suppression
        holder.btnDelete.setOnClickListener { onDelete(record) }
    }

    override fun getItemCount() = records.size

    fun updateData(newRecords: List<LBMRecordEntity>) {
        records = newRecords
        notifyDataSetChanged()
    }
}