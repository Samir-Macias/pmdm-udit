package com.tuapp.appblocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuapp.appblocker.data.BlockedNumber

class BlockedNumberAdapter(
    private var numbersList: List<BlockedNumber>,
    private val onDeleteClick: (BlockedNumber) -> Unit,
    private val onToggleClick: (BlockedNumber, Boolean) -> Unit,
    private val onEditClick: (BlockedNumber) -> Unit
) : RecyclerView.Adapter<BlockedNumberAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPrefix: TextView = view.findViewById(R.id.tvPrefix)
        val tvPriority: TextView = view.findViewById(R.id.tvPriority)
        val switchActive: Switch = view.findViewById(R.id.switchActive)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_number, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blockedNumber = numbersList[position]
        holder.tvPrefix.text = blockedNumber.prefix
        val matchType = if (blockedNumber.matchType == "EXACT") "Número exacto" else "Prefijo"
        holder.tvPriority.text = "$matchType · Prioridad ${blockedNumber.priority}"

        holder.switchActive.setOnCheckedChangeListener(null)
        holder.switchActive.isChecked = blockedNumber.isActive

        holder.switchActive.setOnCheckedChangeListener { _, isChecked ->
            onToggleClick(blockedNumber, isChecked)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(blockedNumber)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(blockedNumber)
        }
    }

    override fun getItemCount() = numbersList.size

    fun updateData(newList: List<BlockedNumber>) {
        numbersList = newList
        notifyDataSetChanged()
    }
}
