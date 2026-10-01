package com.example.clubdeportivo_equipo1

data class Socio(
    val numero: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val activo: Boolean,
    var cuotaAlDia: Boolean,
    val actividades: List<String>
) {
    val nombreCompleto: String
        get() = "$nombre $apellido"

    val numeroFormateado: String
        get() = numero.toString().padStart(4, '0')

    // Muestra el DNI con puntos: 26298456 -> 26.298.456
    val dniFormateado: String
        get() = dni.reversed().chunked(3).joinToString(".").reversed()
}
