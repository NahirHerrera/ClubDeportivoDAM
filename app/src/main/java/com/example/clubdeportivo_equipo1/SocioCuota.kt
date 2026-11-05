package com.example.clubdeportivo_equipo1

// Cliente buscado en Cobrar Cuota (tablas clientes + socios)
data class SocioCuota(
    val idCliente: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val esSocio: Boolean,
    val nroCarnet: Int?,
    // Formato de la base: yyyy-MM-dd (null si no es socio)
    val fechaVencimiento: String?,
    val cuotaAlDia: Boolean
) {
    val nombreCompleto: String
        get() = "$nombre $apellido"

    val nroSocioFormateado: String
        get() = (nroCarnet ?: 0).toString().padStart(4, '0')

    // 26298456 -> 26.298.456
    val dniFormateado: String
        get() = dni.reversed().chunked(3).joinToString(".").reversed()

    // 2026-12-05 -> 05/12/2026
    val vencimientoFormateado: String
        get() = fechaVencimiento?.let { formatearFecha(it) } ?: "-"
}

// Resultado de registrar el pago de una cuota en la base
data class PagoCuota(
    val nroComprobante: Int,
    // Formato de la base: yyyy-MM-dd
    val fechaPago: String,
    val nuevoVencimiento: String
)

// Convierte una fecha de la base (yyyy-MM-dd) a dd/MM/yyyy
fun formatearFecha(fechaBase: String): String {
    val partes = fechaBase.split("-")
    return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fechaBase
}
