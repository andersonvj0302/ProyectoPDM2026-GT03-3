package com.example.autotechs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autotechs.data.local.entity.VehiculoEntity
import com.example.autotechs.databinding.ItemVehiculoCardBinding

class VehiculoAdapter(private val onVehiculoClick: (VehiculoEntity) -> Unit) :
    ListAdapter<VehiculoEntity, VehiculoAdapter.VehiculoViewHolder>(VehiculoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehiculoViewHolder {
        val binding = ItemVehiculoCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VehiculoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehiculoViewHolder, position: Int) {
        val vehiculo = getItem(position)
        holder.bind(vehiculo)
        holder.itemView.setOnClickListener { onVehiculoClick(vehiculo) }
    }

    class VehiculoViewHolder(private val binding: ItemVehiculoCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(vehiculo: VehiculoEntity) {
            binding.tvPlaca.text = vehiculo.placa
            binding.tvMarcaModelo.text = "${vehiculo.marca} ${vehiculo.modelo}"
            binding.tvVin.text = "VIN: ${vehiculo.vin}"
        }
    }

    class VehiculoDiffCallback : DiffUtil.ItemCallback<VehiculoEntity>() {
        override fun areItemsTheSame(oldItem: VehiculoEntity, newItem: VehiculoEntity): Boolean {
            return oldItem.vin == newItem.vin
        }

        override fun areContentsTheSame(oldItem: VehiculoEntity, newItem: VehiculoEntity): Boolean {
            return oldItem == newItem
        }
    }
}
