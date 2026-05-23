package com.example.autotechs.data.remote

/**
 * Sealed class genérica para encapsular el resultado de operaciones de red.
 * Permite manejar de forma consistente los estados de las peticiones API
 * en todos los repositorios y ViewModels.
 *
 * Uso:
 *   when (result) {
 *       is NetworkResult.Success -> // usar result.data
 *       is NetworkResult.Error -> // mostrar result.message
 *       is NetworkResult.Loading -> // mostrar indicador de carga
 *   }
 */
sealed class NetworkResult<T> {
    /** La petición fue exitosa y contiene los datos */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /** La petición falló con un mensaje de error */
    data class Error<T>(val message: String) : NetworkResult<T>()

    /** La petición está en progreso */
    class Loading<T> : NetworkResult<T>()
}
