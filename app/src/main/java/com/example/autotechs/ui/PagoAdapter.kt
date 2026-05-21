package com.example.autotechs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autotechs.data.local.entity.PagoEntity
import com.example.autotechs.databinding.ItemPagoCardBinding

class PagoAdapter(private val onPagoClick: (PagoEntity) -> Unit) :
    ListAdapter<PagoEntity, PagoAdapter.PagoViewHolder>(PagoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val binding = ItemPagoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PagoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        val pago = getItem(position)
        holder.bind(pago)
        holder.itemView.setOnClickListener { onPagoClick(pago) }
    }

    class PagoViewHolder(private val binding: ItemPagoCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pago: PagoEntity) {
            binding.tvTransaccionId.text = "Transacción #${pago.id}"
            binding.tvExpediente.text = "Expediente Asociado: #${pago.expedienteId}"
            binding.tvMetodo.text = "Método: ${pago.modalidad}"
            binding.tvMonto.text = "$${String.format("%.2f", pago.monto)}"
        }
    }

    class PagoDiffCallback : DiffUtil.ItemCallback<PagoEntity>() {
        override fun areItemsTheSame(oldItem: PagoEntity, newItem: PagoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PagoEntity, newItem: PagoEntity): Boolean {
            return oldItem == newItem
        }
    }
}
