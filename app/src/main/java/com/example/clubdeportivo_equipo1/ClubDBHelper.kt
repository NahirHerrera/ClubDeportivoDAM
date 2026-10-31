package com.example.clubdeportivo_equipo1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        listOf(
            "inscripcion_actividad", "carnet", "pagos", "cuota", "inscripcion",
            "nosocios", "actividades", "socios", "clientes", "usuarios", "roles", "socios"
        ).forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }
}