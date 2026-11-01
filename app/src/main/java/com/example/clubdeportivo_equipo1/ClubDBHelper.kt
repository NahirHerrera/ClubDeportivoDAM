package com.example.clubdeportivo_equipo1

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
                tipoDoc TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                aptoFisico INTEGER NOT NULL DEFAULT 0
            )""".trimIndent()
        )

        db.execSQL(
            """CREATE TABLE socios(
                idCliente INTEGER PRIMARY KEY,
                nroCarnet INTEGER NOT NULL,
                fecha_vencimiento_cuota TEXT NOT NULL,
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

        db.execSQL(
            """CREATE TABLE nosocios(
                idCliente INTEGER PRIMARY KEY,
                idActividad INTEGER NOT NULL,
                fechaActividad TEXT NOT NULL,
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
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

    fun listarClientes(dni: String): List<ClienteListado> {
        val lista = mutableListOf<ClienteListado>()
        readableDatabase.rawQuery(
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
        ).use { c ->
            while (c.moveToNext()) lista.add(
                ClienteListado(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6))
            )
        }
        return lista
    }

    fun insertarCliente(nombre: String, apellido: String, tipoDoc: String, dni: String): Long =
        writableDatabase.insert("clientes", null, ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("tipoDoc", tipoDoc)
            put("dni", dni)
        })

    fun insertarSocio(idCliente: Int, nroCarnet: Int, vencimiento: String): Long =
        writableDatabase.insert("socios", null, ContentValues().apply {
            put("idCliente", idCliente)
            put("nroCarnet", nroCarnet)
            put("fecha_vencimiento_cuota", vencimiento)
        })

    fun insertarActividad(nombre: String, costo: Double): Long =
        writableDatabase.insert("actividades", null, ContentValues().apply {
            put("nombreActividad", nombre)
            put("costo_pase_diario", costo)
        })

    fun inscribir(idCliente: Int, idActividad: Int, formaPago: String, monto: Double, fecha: String): Long =
        writableDatabase.insert("inscripcion_actividad", null, ContentValues().apply {
            put("idCliente", idCliente)
            put("idActividad", idActividad)
            put("formaPago", formaPago)
            put("monto", monto)
            put("fechaInscripcion", fecha)
        })

}