package com.tuapp.appblocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuapp.appblocker.data.WhitelistNumber

class WhitelistNumberAdapter(
    private var numbers: List<WhitelistNumber>,
    private val onEditClick: (WhitelistNumber) -> Unit,
    private val onDeleteClick: (WhitelistNumber) -> Unit
) : RecyclerView.Adapter<WhitelistNumberAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prefix: TextView = view.findViewById(R.id.tvWhitelistPrefix)
        val edit: Button = view.findViewById(R.id.btnEditWhitelist)
        val delete: Button = view.findViewById(R.id.btnDeleteWhitelist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_whitelist_number, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = numbers[position]
        holder.prefix.text = number.prefix
        holder.edit.setOnClickListener { onEditClick(number) }
        holder.delete.setOnClickListener { onDeleteClick(number) }
    }

    override fun getItemCount(): Int = numbers.size

    fun updateData(newNumbers: List<WhitelistNumber>) {
        numbers = newNumbers
        notifyDataSetChanged()
    }
}
