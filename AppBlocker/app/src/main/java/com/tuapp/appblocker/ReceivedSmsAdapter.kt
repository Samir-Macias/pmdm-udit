package com.tuapp.appblocker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuapp.appblocker.data.ReceivedSms
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReceivedSmsAdapter(
    private var messages: List<ReceivedSms>,
    private val onOpen: (ReceivedSms) -> Unit,
    private val onDelete: (ReceivedSms) -> Unit
) : RecyclerView.Adapter<ReceivedSmsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sender: TextView = view.findViewById(R.id.tvSmsSender)
        val date: TextView = view.findViewById(R.id.tvSmsDate)
        val preview: TextView = view.findViewById(R.id.tvSmsPreview)
        val badge: TextView = view.findViewById(R.id.tvSmsClassification)
        val delete: Button = view.findViewById(R.id.btnDeleteSms)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_received_sms, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        val dateFormat = SimpleDateFormat("dd/MM · HH:mm", Locale.getDefault())
        holder.sender.text = message.sender
        holder.date.text = dateFormat.format(Date(message.receivedAt))
        holder.preview.text = message.body
        val suspicious = message.classification == "SOSPECHOSO"
        holder.badge.text = if (suspicious) "SOSPECHOSO" else "NORMAL"
        holder.badge.setTextColor(
            holder.itemView.context.getColor(
                if (suspicious) R.color.status_blocked else R.color.status_allowed
            )
        )
        holder.itemView.setOnClickListener { onOpen(message) }
        holder.delete.setOnClickListener { onDelete(message) }
    }

    override fun getItemCount(): Int = messages.size

    fun updateData(newMessages: List<ReceivedSms>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
