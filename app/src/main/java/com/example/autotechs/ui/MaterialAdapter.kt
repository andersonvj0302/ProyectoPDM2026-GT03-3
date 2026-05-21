package com.example.autotechs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.databinding.ItemMaterialCardBinding

class MaterialAdapter(private val onMaterialClick: (MaterialEntity) -> Unit) :
    ListAdapter<MaterialEntity, MaterialAdapter.MaterialViewHolder>(MaterialDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = getItem(position)
        holder.bind(material)
        holder.itemView.setOnClickListener { onMaterialClick(material) }
    }

    class MaterialViewHolder(private val binding: ItemMaterialCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(material: MaterialEntity) {
            binding.tvNombre.text = material.nombre
            
            val faseNombre = when (material.faseReparacionId) {
                1 -> "Desarme"
                2 -> "Enderezado"
                3 -> "Preparación"
                4 -> "Pintura"
                5 -> "Secado y Pulido"
                6 -> "Armado y Control"
                else -> "General"
            }
            binding.tvDetalleAsociado.text = "Exp: #${material.expedienteId} - $faseNombre"
            binding.tvCosto.text = "Costo Unitario: $${String.format("%.2f", material.costoUnitario)}"
            binding.tvCantidad.text = "${material.cantidad} u"
        }
    }

    class MaterialDiffCallback : DiffUtil.ItemCallback<MaterialEntity>() {
        override fun areItemsTheSame(oldItem: MaterialEntity, newItem: MaterialEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MaterialEntity, newItem: MaterialEntity): Boolean {
            return oldItem == newItem
        }
    }
}
