package com.example.clubdeportivo_equipo1

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

// Datos de prueba en memoria hasta que se conecte la base de datos
object SociosRepository {

    const val VALOR_CUOTA = 30000.0
    const val DESCUENTO_EFECTIVO = 0.10

    // Los socios que deben la cuota no tienen actividades: no pueden inscribirse hasta pagar
    private val socios = mutableListOf(
        Socio(1, "Juan", "Perez", "26298456", true, true, "15/03/2025",
            actividades = mutableListOf("Musculación", "Funcional")),
        Socio(2, "María", "Gómez", "30123456", true, false, "02/06/2025"),
        Socio(3, "Carlos", "López", "28555111", true, false, "20/08/2025"),
        Socio(4, "Lucía", "Fernández", "35987654", true, true, "10/01/2026",
            actividades = mutableListOf("Spinning", "Zumba")),
        Socio(5, "Martín", "Rodríguez", "33444222", false, false, "05/11/2024"),
        Socio(6, "Sofía", "Martínez", "40111222", true, false, "18/04/2026"),
        Socio(7, "Diego", "Sánchez", "27666333", true, true, "01/07/2026")
    )

    private var ultimoComprobante = 1000

    fun montoEfectivo(): Double = VALOR_CUOTA * (1 - DESCUENTO_EFECTIVO)

    fun buscarPorDni(dni: String): Socio? = socios.find { it.dni == dni }

    // Marca la cuota como paga y devuelve el número de comprobante generado
    fun registrarPago(socio: Socio): Int {
        socio.cuotaAlDia = true
        ultimoComprobante++
        return ultimoComprobante
    }

    fun renovarApto(dni: String) {
        buscarPorDni(dni)?. aptoFisicoFecha =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    // ENDPOINT ABIERTO - lo va a usar el menú Actividad para inscribir al cliente.
    // Solo se puede inscribir si tiene la cuota al día. Devuelve false si no se pudo.
    fun inscribirEnActividad(socio: Socio, actividad: String): Boolean {
        if (!socio.activo || !socio.cuotaAlDia) return false
        if (actividad !in socio.actividades) socio.actividades.add(actividad)
        return true
    }

    fun deudores(): List<Socio> = socios.filter { it.activo && !it.cuotaAlDia }
}
