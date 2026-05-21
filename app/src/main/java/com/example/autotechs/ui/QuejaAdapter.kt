package com.example.autotechs.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autotechs.data.local.entity.QuejaEntity
import com.example.autotechs.databinding.ItemQuejaCardBinding

class QuejaAdapter(private val onQuejaClick: (QuejaEntity) -> Unit) :
    ListAdapter<QuejaEntity, QuejaAdapter.QuejaViewHolder>(QuejaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuejaViewHolder {
        val binding = ItemQuejaCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuejaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuejaViewHolder, position: Int) {
        val queja = getItem(position)
        holder.bind(queja)
        holder.itemView.setOnClickListener { onQuejaClick(queja) }
    }

    class QuejaViewHolder(private val binding: ItemQuejaCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(queja: QuejaEntity) {
            binding.tvQuejaId.text = "Queja #${queja.id} (Exp: #${queja.expedienteId})"
            binding.tvDescripcion.text = queja.descripcion
            
            if (queja.justificada) {
                binding.tvEstado.text = "Estado: JUSTIFICADA"
                binding.tvEstado.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                binding.tvEstado.text = "Estado: NO PROCEDE"
                binding.tvEstado.setTextColor(Color.parseColor("#F44336"))
            }
            
            binding.tvReintegro.text = "Reintegro: $${String.format("%.2f", queja.montoReintegro)}"
        }
    }

    class QuejaDiffCallback : DiffUtil.ItemCallback<QuejaEntity>() {
        override fun areItemsTheSame(oldItem: QuejaEntity, newItem: QuejaEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QuejaEntity, newItem: QuejaEntity): Boolean {
            return oldItem == newItem
        }
    }
}
