package com.wingspan.groundowner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingspan.groundowner.R

class DaysAdapter(
    private val days: List<String>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    private val selectedDays = mutableListOf<String>()

    inner class DayViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val dayText: TextView = view.findViewById(R.id.dayText)
        val dayItem: View = view.findViewById(R.id.dayItem)
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_days_selector, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.dayText.text = day

        holder.dayItem.isSelected = selectedDays.contains(day)

        holder.dayItem.setOnClickListener {
            if (selectedDays.contains(day)) {
                selectedDays.remove(day)
            } else {
                selectedDays.add(day)
            }
            notifyItemChanged(position)
            onSelectionChanged(selectedDays)
        }
    }

    override fun getItemCount(): Int = days.size
}
