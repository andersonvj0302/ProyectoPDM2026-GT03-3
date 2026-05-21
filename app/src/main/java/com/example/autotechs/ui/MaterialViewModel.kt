package com.example.autotechs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotechs.data.local.entity.MaterialEntity
import com.example.autotechs.data.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MaterialViewModel(private val repository: MaterialRepository) : ViewModel() {

    val materiales: Flow<List<MaterialEntity>> = repository.allMateriales

    fun insertMaterial(nombre: String, cantidad: Int, costoUnitario: Double, expedienteId: Int, faseReparacionId: Int) {
        viewModelScope.launch {
            val material = MaterialEntity(
                nombre = nombre,
                cantidad = cantidad,
                costoUnitario = costoUnitario,
                expedienteId = expedienteId,
                faseReparacionId = faseReparacionId
            )
            repository.insert(material)
        }
    }

    fun updateMaterial(id: Int, nombre: String, cantidad: Int, costoUnitario: Double, expedienteId: Int, faseReparacionId: Int) {
        viewModelScope.launch {
            val material = MaterialEntity(
                id = id,
                nombre = nombre,
                cantidad = cantidad,
                costoUnitario = costoUnitario,
                expedienteId = expedienteId,
                faseReparacionId = faseReparacionId
            )
            repository.update(material)
        }
    }

    fun deleteMaterial(material: MaterialEntity) {
        viewModelScope.launch {
            repository.delete(material)
        }
    }
}
