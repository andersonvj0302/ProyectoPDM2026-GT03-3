package com.example.autotechs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autotechs.data.local.entity.ClienteEntity
import com.example.autotechs.databinding.ItemClienteCardBinding

class ClienteAdapter(private val onClienteClick: (ClienteEntity) -> Unit) :
    ListAdapter<ClienteEntity, ClienteAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = getItem(position)
        holder.bind(cliente)
        holder.itemView.setOnClickListener { onClienteClick(cliente) }
    }

    class ClienteViewHolder(private val binding: ItemClienteCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cliente: ClienteEntity) {
            binding.tvNombre.text = cliente.nombre
            binding.tvDetalle.text = "DUI: ${cliente.dui} | Tel: ${cliente.telefono}"
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<ClienteEntity>() {
        override fun areItemsTheSame(oldItem: ClienteEntity, newItem: ClienteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ClienteEntity, newItem: ClienteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
