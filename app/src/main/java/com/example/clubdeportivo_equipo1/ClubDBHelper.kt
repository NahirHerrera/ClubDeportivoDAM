package com.example.clubdeportivo_equipo1

import android.R
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues


class ClubDBHelper(context: Context) : SQLiteOpenHelper(context, "Club.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """CREATE TABLE roles(
                idRol INTEGER PRIMARY KEY,
                nombreRol TEXT NOT NULL
            )""".trimIndent()
        )

        db.execSQL("INSERT INTO roles(idRol, nombreRol) VALUES (1, 'Administrador'), (2, 'Empleado')")

        db.execSQL(
            """CREATE TABLE usuarios(
                idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                idRol INTEGER NOT NULL,
                FOREIGN KEY (idRol) REFERENCES roles(idRol)
            )""".trimIndent()
        )

        db.execSQL("INSERT INTO Usuarios(Usuario, Password, idRol) VALUES ('admin@fitnesscenter.com', 'Club1234', 1), ('empleado@fitnesscenter.com', 'Club1234', 2)")

        db.execSQL(
            """CREATE TABLE clientes(
                idCliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                tipoCliente TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                aptoFisico INTEGER NOT NULL DEFAULT 0
            )""".trimIndent()
        )

        db.execSQL(
            """CREATE TABLE socios(
                idCliente INTEGER PRIMARY KEY,
                nroCarnet INTEGER NOT NULL,
                fecha_vencimiento_cuota DATE,
                FOREIGN KEY (idCliente) REFERENCES clientes(idCliente)
            )""".trimIndent()
        )

        db.execSQL(
            """CREATE TABLE actividades(
                idActividad INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreActividad TEXT NOT NULL,
                costo_pase_diario REAL NOT NULL
            )""".trimIndent()
        )

        db.execSQL("INSERT INTO actividades(nombreActividad, costo_pase_diario) VALUES ('Spinnig', 10000), ('Natacion', 15000), ('Yoga', 12000),('Zumba', 8000)")

        db.execSQL(
            """CREATE TABLE nosocios(
                idCliente INTEGER PRIMARY KEY,
                idActividad INTEGER,
                fechaActividad TEXT,
                FOREIGN KEY (idCliente) REFERENCES clientes(idCliente),
                FOREIGN KEY (idActividad) REFERENCES actividades(idActividad)
            )""".trimIndent()
        )

        db.execSQL(
            """CREATE TABLE inscripcion(
                idInscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
                idCliente INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY (idCliente) REFERENCES clientes(idCliente)
                )""".trimIndent()
        )

        db.execSQL(
            """CREATE TABLE cuota(
                idCuota INTEGER PRIMARY KEY AUTOINCREMENT,
                idCliente INTEGER NOT NULL,
                monto REAL NOT NULL,
                fechaVencimiento TEXT NOT NULL,
                estado TEXT NOT NULL,
                FOREIGN KEY (idCliente) REFERENCES socios(idCliente)
                )""".trimIndent()
        )

        db.execSQL("""
            CREATE TABLE pagos(
            idPago INTEGER PRIMARY KEY AUTOINCREMENT,
            idCuota INTEGER,
            idCliente INTEGER NOT NULL,
            monto REAL NOT NULL,
            fechaPago TEXT NOT NULL,
            nroComprobante INTEGER NOT NULL,
            FOREIGN KEY (idCuota) REFERENCES cuota(idCuota),
            FOREIGN KEY (idCliente) REFERENCES clientes(idCliente)
            )""".trimIndent()
        )

        db.execSQL("""
            CREATE TABLE carnet(
            idCarnet INTEGER PRIMARY KEY AUTOINCREMENT,
            nroCarnet INTEGER NOT NULL,
            fechaEmision TEXT NOT NULL,
            idCliente INTEGER NOT NULL,
            FOREIGN KEY (idCliente) REFERENCES clientes(idCliente)
            )""".trimIndent()
        )

        db.execSQL("""
            CREATE TABLE inscripcion_actividad(
            idInscripcion INTEGER PRIMARY KEY AUTOINCREMENT,
            idCliente INTEGER NOT NULL,
            idActividad INTEGER NOT NULL,
            formaPago TEXT NOT NULL,
            monto REAL NOT NULL,
            fechaInscripcion TEXT NOT NULL,
            FOREIGN KEY (idCliente) REFERENCES clientes(idCliente),
            FOREIGN KEY (idActividad) REFERENCES actividades(idActividad),
            UNIQUE (idCliente, idActividad)
            )""".trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not get implemented")
    }

    // ---------- Login para administrador y empleado de Fitness Center -------
    fun validarLogin(usuario: String, password: String): String? =
        readableDatabase.rawQuery(
            """SELECT r.nombreRol
           FROM usuarios u
           JOIN roles r ON r.idRol = u.idRol
           WHERE u.usuario = ? AND u.password = ?""",
            arrayOf(usuario, password)
        ).use { if (it.moveToFirst()) it.getString(0) else null }

    // ---------- Listado de clientes (Nahir) ----------

    fun insertarCliente(nombre: String, apellido: String, tipoCliente: String, dni: String, aptoFisico: Boolean): Long {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("tipoCliente", tipoCliente)
            put("dni", dni)
            put("aptoFisico", aptoFisico)
        }

        val resultado = db.insert("clientes", null, valores)
        db.close()
        return resultado
    }

    fun insertarSocio(idCliente: Int, nroCarnet: Int, vencimiento: String?): Long {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put("idCliente", idCliente)
            put("nroCarnet", nroCarnet)
            put("fecha_vencimiento_cuota", vencimiento)
        }

        val resultado = db.insert("socios", null, valores)
        db.close()
        return resultado
    }

    fun proximoNroCarnet(): Int =
        readableDatabase.rawQuery(
            "SELECT COALESCE(MAX(nroCarnet), 1000) + 1 FROM socios", null
        ).use { it.moveToFirst(); it.getInt(0) }

    fun insertarNoSocio(idCliente: Int, idActividad: Int?, fechaActividad: String?): Long {
        val db = writableDatabase

        val valores = ContentValues().apply{
            put("idCliente", idCliente)
            put("idActividad", idActividad)
            put("fechaActividad", fechaActividad)
        }
        val resultado = db.insert("nosocios", null, valores)
        db.close()
        return resultado
    }

    fun insertarActividad(nombre: String, costo: Double): Long {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put("nombreActividad", nombre)
            put("costo_pase_diario", costo)
        }

        val resultado = db.insert("actividades", null, valores)
        db.close()
        return resultado
    }

    fun inscribirActividad(idCliente: Int, idActividad: Int, formaPago: String, monto: Double, fecha: String): Long {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put("idCliente", idCliente)
            put("idActividad", idActividad)
            put("formaPago", formaPago)
            put("monto", monto)
            put("fechaInscripcion", fecha)
        }

        val resultado = db.insert("inscripcion_actividad", null, valores)
        db.close()
        return resultado
    }
    fun listarClientes(dni: String): List<ClienteListado> {
        val db = readableDatabase
        val lista = mutableListOf<ClienteListado>()

        val cursor = db.rawQuery(
            """SELECT c.idCliente,
              CASE WHEN s.idCliente IS NOT NULL THEN 'Socio' ELSE 'No socio' END,
              c.nombre, c.apellido, c.dni,
              MIN(i.fechaInscripcion),
              GROUP_CONCAT(a.nombreActividad, char(10))
       FROM clientes c
       LEFT JOIN socios s ON s.idCliente = c.idCliente
       LEFT JOIN inscripcion_actividad i ON i.idCliente = c.idCliente
       LEFT JOIN actividades a ON a.idActividad = i.idActividad
       WHERE c.dni LIKE ?
         AND (s.idCliente IS NULL OR s.fecha_vencimiento_cuota >= date('now'))
       GROUP BY c.idCliente
       ORDER BY c.apellido, c.nombre""", arrayOf("$dni%")
        )

        while (cursor.moveToNext()) {
            val cliente = ClienteListado(
                cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)
            )
            lista.add(cliente)
        }

        cursor.close()
        return lista
    }

    // ---------- Actividades (Alex) ----------

    fun obtenerActividades(): MutableList<Actividades>{
        val lista = mutableListOf<Actividades>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT idActividad, nombreActividad, costo_pase_diario FROM actividades ORDER BY id DESC", null)

        cursor.use{
            while(it.moveToNext()){
                    lista.add(Actividades(
                        Id = it.getInt(0),
                        nombre = it.getString(1),
                        costo = it.getDouble(2)
                        )
                    )
            }
            db.close()
            return lista
        }

    }

    fun buscarCliente(dni: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT nombre, apellido FROM clientes WHERE dni = ?", arrayOf(dni))


        var resultado: String? = null

        cursor.use {
            if (it.moveToFirst()) {
                resultado = "${it.getString(0)} ${it.getString(1)}"
            }
        }

        db.close()
        return resultado
    }

    // ---------- Cobrar cuota (Gastón) ----------
    // La cuota está al día si socios.fecha_vencimiento_cuota es hoy o una fecha posterior.

    // Busca un cliente por DNI e indica si es socio y si tiene la cuota al día. Null si no existe.
    fun buscarSocioCuota(dni: String): SocioCuota? =
        readableDatabase.rawQuery(
            """SELECT c.idCliente, c.nombre, c.apellido, c.dni,
                      s.idCliente IS NOT NULL,
                      s.nroCarnet,
                      s.fecha_vencimiento_cuota,
                      s.fecha_vencimiento_cuota >= date('now', 'localtime')
               FROM clientes c
               LEFT JOIN socios s ON s.idCliente = c.idCliente
               WHERE c.dni = ?""",
            arrayOf(dni)
        ).use {
            if (!it.moveToFirst()) {
                null
            } else {
                val esSocio = it.getInt(4) == 1
                SocioCuota(
                    idCliente = it.getInt(0),
                    nombre = it.getString(1),
                    apellido = it.getString(2),
                    dni = it.getString(3),
                    esSocio = esSocio,
                    nroCarnet = if (esSocio) it.getInt(5) else null,
                    fechaVencimiento = if (esSocio) it.getString(6) else null,
                    cuotaAlDia = esSocio && it.getInt(7) == 1
                )
            }
        }

    // Registra el pago de la cuota en una sola transacción:
    // 1) cuota pagada, 2) pago con su N° de comprobante, 3) nuevo vencimiento del socio (hoy + 1 mes).
    // Devuelve null si algo falla (en ese caso no se guarda nada).
    fun registrarPagoCuota(idCliente: Int, montoCuota: Double, montoPagado: Double): PagoCuota? {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val (hoy, nuevoVencimiento) = db.rawQuery(
                "SELECT date('now', 'localtime'), date('now', 'localtime', '+1 month')", null
            ).use { it.moveToFirst(); it.getString(0) to it.getString(1) }

            val idCuota = db.insert("cuota", null, ContentValues().apply {
                put("idCliente", idCliente)
                put("monto", montoCuota)
                put("fechaVencimiento", nuevoVencimiento)
                put("estado", "Pagada")
            })
            if (idCuota == -1L) return null

            // Los comprobantes arrancan en 1001
            val nroComprobante = db.rawQuery(
                "SELECT COALESCE(MAX(nroComprobante), 1000) + 1 FROM pagos", null
            ).use { it.moveToFirst(); it.getInt(0) }

            val idPago = db.insert("pagos", null, ContentValues().apply {
                put("idCuota", idCuota)
                put("idCliente", idCliente)
                put("monto", montoPagado)
                put("fechaPago", hoy)
                put("nroComprobante", nroComprobante)
            })
            if (idPago == -1L) return null

            val filas = db.update(
                "socios",
                ContentValues().apply { put("fecha_vencimiento_cuota", nuevoVencimiento) },
                "idCliente = ?",
                arrayOf(idCliente.toString())
            )
            if (filas != 1) return null

            db.setTransactionSuccessful()
            return PagoCuota(nroComprobante, hoy, nuevoVencimiento)
        } finally {
            db.endTransaction()
        }
    }

    // Datos de prueba para Cobrar Cuota. Usa INSERT OR IGNORE, así que se puede llamar
    // muchas veces sin duplicar nada. Los vencimientos se calculan la primera vez que se cargan.
    fun cargarDatosPruebaCobrarCuota() {
        // nombre, apellido, dni, nroCarnet (null = no socio), vencimiento relativo a hoy
        val datos: List<List<Any?>> = listOf(
            listOf("Juan", "Perez", "26298456", 1, "+20 days"),
            listOf("María", "Gómez", "30123456", 2, "-10 days"),
            listOf("Carlos", "López", "28555111", 3, "-40 days"),
            listOf("Lucía", "Fernández", "35987654", 4, "+1 month"),
            listOf("Martín", "Rodríguez", "33444222", null, null),
            listOf("Sofía", "Martínez", "40111222", 6, "-3 days"),
            listOf("Diego", "Sánchez", "27666333", 7, "+5 days")
        )

        val db = writableDatabase
        db.beginTransaction()
        try {
            for ((nombre, apellido, dni, nroCarnet, vencimiento) in datos) {
                db.execSQL(
                    "INSERT OR IGNORE INTO clientes(nombre, apellido, tipoCliente, dni) VALUES (?, ?, ?, ?)",
                    arrayOf(nombre, apellido, dni)
                )
                if (nroCarnet != null) {
                    db.execSQL(
                        """INSERT OR IGNORE INTO socios(idCliente, nroCarnet, fecha_vencimiento_cuota)
                           SELECT idCliente, ?, date('now', 'localtime', ?) FROM clientes WHERE dni = ?""",
                        arrayOf(nroCarnet, vencimiento, dni)
                    )
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}