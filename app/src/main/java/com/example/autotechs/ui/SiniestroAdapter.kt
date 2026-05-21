package com.example.autotechs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autotechs.data.local.entity.SiniestroWithVehiculo
import com.example.autotechs.databinding.ItemSiniestroCardBinding

class SiniestroAdapter(private val onSiniestroClick: (SiniestroWithVehiculo) -> Unit) :
    ListAdapter<SiniestroWithVehiculo, SiniestroAdapter.SiniestroViewHolder>(SiniestroDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiniestroViewHolder {
        val binding = ItemSiniestroCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SiniestroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SiniestroViewHolder, position: Int) {
        val siniestro = getItem(position)
        holder.bind(siniestro)
        holder.itemView.setOnClickListener { onSiniestroClick(siniestro) }
    }

    class SiniestroViewHolder(private val binding: ItemSiniestroCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(siniestro: SiniestroWithVehiculo) {
            binding.tvVehiculo.text = "${siniestro.marca} ${siniestro.modelo}"
            binding.tvPlaca.text = "Placa: ${siniestro.placa}"
            
            // Construir resumen de daños
            val danosList = mutableListOf<String>()
            if (siniestro.danosEstructurales) danosList.add("Estructural")
            if (siniestro.danosEsteticos) danosList.add("Estético")
            if (siniestro.danosMecanicos) danosList.add("Mecánico")
            
            val danosText = if (danosList.isNotEmpty()) {
                "Daños: " + danosList.joinToString(", ")
            } else {
                "Sin daños aparentes"
            }
            binding.tvDanos.text = danosText
            
            // El estado por defecto será TALLER
            binding.tvEstado.text = "EXP: #${siniestro.id}\nTALLER"
        }
    }

    class SiniestroDiffCallback : DiffUtil.ItemCallback<SiniestroWithVehiculo>() {
        override fun areItemsTheSame(oldItem: SiniestroWithVehiculo, newItem: SiniestroWithVehiculo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SiniestroWithVehiculo, newItem: SiniestroWithVehiculo): Boolean {
            return oldItem == newItem
        }
    }
}
