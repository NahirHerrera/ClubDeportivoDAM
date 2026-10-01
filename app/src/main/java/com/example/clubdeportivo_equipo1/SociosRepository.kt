package com.example.clubdeportivo_equipo1

// Datos de prueba en memoria hasta que se conecte la base de datos
object SociosRepository {

    const val VALOR_CUOTA = 30000.0
    const val DESCUENTO_EFECTIVO = 0.10

    private val socios = mutableListOf(
        Socio(1, "Juan", "Perez", "26298456", true, true, listOf("Musculación", "Funcional"), "15/03/2025"),
        Socio(2, "María", "Gómez", "30123456", true, false, listOf("Yoga", "Pilates"), "02/06/2025"),
        Socio(3, "Carlos", "López", "28555111", true, false, listOf("Natación"), "20/08/2025"),
        Socio(4, "Lucía", "Fernández", "35987654", true, true, listOf("Spinning", "Zumba"), "10/01/2026"),
        Socio(5, "Martín", "Rodríguez", "33444222", false, false, listOf("Boxeo"), "05/11/2024"),
        Socio(6, "Sofía", "Martínez", "40111222", true, false, listOf("Crossfit", "Musculación"), "18/04/2026"),
        Socio(7, "Diego", "Sánchez", "27666333", true, true, listOf("Natación", "Spinning"), "01/07/2026")
    )

    private var ultimoComprobante = 1000

    fun montoEfectivo(): Double = VALOR_CUOTA * (1 - DESCUENTO_EFECTIVO)

    fun buscarPorDni(dni: String): Socio? = socios.find { it.dni == dni }

    fun listarActivos(): List<Socio> = socios.filter { it.activo }.sortedBy { it.numero }

    // Filtra los activos cuyo DNI contiene el texto buscado (vacío = todos)
    fun buscarActivosPorDni(texto: String): List<Socio> =
        listarActivos().filter { it.dni.contains(texto) }

    // Marca la cuota como paga y devuelve el número de comprobante generado
    fun registrarPago(socio: Socio): Int {
        socio.cuotaAlDia = true
        ultimoComprobante++
        return ultimoComprobante
    }
}
