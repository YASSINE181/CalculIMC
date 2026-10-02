package com.example.calculimc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.calculimc.databinding.ItemHistoryBinding
import java.util.Locale

class HistoryAdapter(
    private val records: MutableList<ImcRecord> = mutableListOf()
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val record = records[position]
        with(holder.binding) {
            textHistoryCategory.text = record.categoryText
            textHistoryValue.text = String.format(Locale.US, "%.2f", record.imcValue)
            textHistoryDetails.text = String.format(
                Locale.FRANCE,
                "Poids: %.1f kg | Taille: %.2f m",
                record.poidsKg,
                record.tailleM
            )
            viewCategoryColor.setBackgroundColor(
                ContextCompat.getColor(root.context, record.categoryColorRes)
            )
        }
    }

    override fun getItemCount(): Int = records.size

    fun addRecord(record: ImcRecord) {
        records.add(0, record) // Insert at top
        notifyItemInserted(0)
    }

    fun clearHistory() {
        val size = records.size
        records.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getRecords(): List<ImcRecord> = records
}
