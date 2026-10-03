package com.example.clubdeportivo_equipo1

data class Socio(
    val numero: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val activo: Boolean,
    var cuotaAlDia: Boolean,
    val fechaInscripcion: String,
    val tipoCliente: String = "Socio",
    // Se completa desde el menú Actividad (solo si tiene la cuota al día)
    val actividades: MutableList<String> = mutableListOf(),
    var aptoFisicoFecha: String = fechaInscripcion
) {
    val nombreCompleto: String
        get() = "$nombre $apellido"

    val numeroFormateado: String
        get() = numero.toString().padStart(4, '0')

    // Muestra el DNI con puntos: 26298456 -> 26.298.456
    val dniFormateado: String
        get() = dni.reversed().chunked(3).joinToString(".").reversed()
}
