package com.example.clubdeportivo_equipo1

// Datos de prueba en memoria hasta que se conecte la base de datos
object SociosRepository {

    const val VALOR_CUOTA = 30000.0

    private val socios = mutableListOf(
        Socio(1, "Juan", "Perez", "26298456", true, true, listOf("Musculación", "Funcional")),
        Socio(2, "María", "Gómez", "30123456", true, false, listOf("Yoga", "Pilates")),
        Socio(3, "Carlos", "López", "28555111", true, false, listOf("Natación")),
        Socio(4, "Lucía", "Fernández", "35987654", true, true, listOf("Spinning", "Zumba", "Funcional")),
        Socio(5, "Martín", "Rodríguez", "33444222", false, false, listOf("Boxeo")),
        Socio(6, "Sofía", "Martínez", "40111222", true, false, listOf("Crossfit", "Musculación")),
        Socio(7, "Diego", "Sánchez", "27666333", true, true, listOf("Natación", "Spinning"))
    )

    private var ultimoComprobante = 1000

    fun buscarPorDni(dni: String): Socio? = socios.find { it.dni == dni }

    fun listarActivos(): List<Socio> = socios.filter { it.activo }.sortedBy { it.apellido }

    // Marca la cuota como paga y devuelve el número de comprobante generado
    fun registrarPago(socio: Socio): Int {
        socio.cuotaAlDia = true
        ultimoComprobante++
        return ultimoComprobante
    }
}
