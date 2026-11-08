package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarClienteActivity : AppCompatActivity() {

    private lateinit var db: ClubDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ClubDBHelper(this)

        val spinner = findViewById<Spinner>(R.id.TipoCliente)
        val opciones = listOf("Socio", "No socio")
        val textNombre: EditText = findViewById(R.id.Nombre)
        val textApellido: EditText = findViewById(R.id.Apellido)
        val textDocumento: EditText = findViewById(R.id.Documento)
        val checkAptoFisico: CheckBox = findViewById(R.id.checkAptoFisico)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val soloTextoFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.isEmpty()) return@InputFilter null
            val pattern = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
            if (pattern.matches(source)) null else ""
        }

        textNombre.filters = arrayOf(soloTextoFilter)
        textApellido.filters = arrayOf(soloTextoFilter)


        findViewById<Button>(R.id.btnRegistrarCliente).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.Nombre).text.toString().trim()
            val apellido = findViewById<EditText>(R.id.Apellido).text.toString().trim()
            val documento = findViewById<EditText>(R.id.Documento).text.toString().trim()

            when {
                nombre.isEmpty() -> {
                    Toast.makeText(this, "Complete el nombre", Toast.LENGTH_SHORT).show()
                    textNombre.requestFocus()
                }

                apellido.isEmpty() -> {
                    Toast.makeText(this, "Complete el apellido", Toast.LENGTH_SHORT).show()
                    textApellido.requestFocus()
                }

                documento.isEmpty() -> {
                    Toast.makeText(this, "Complete el documento", Toast.LENGTH_SHORT).show()
                    textDocumento.requestFocus()
                }

                !checkAptoFisico.isChecked -> {
                    Toast.makeText(this, "Debe presentar el apto físico vigente", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val tipoCliente = spinner.selectedItem.toString()
                    val aptoFisico = checkAptoFisico.isChecked

                    val idCliente = db.insertarCliente(nombre, apellido, tipoCliente, documento, aptoFisico)

                    if (idCliente == -1L) {
                        Toast.makeText(this, "No se pudo registrar. Verifique que el documento no exista", Toast.LENGTH_SHORT).show()
                        textDocumento.requestFocus()
                        return@setOnClickListener
                    }

                    // 2. Según el tipo, se registra como socio o no socio
                    val resultado = if (tipoCliente == "Socio") {
                        db.insertarSocio(idCliente.toInt(), db.proximoNroCarnet(), null)
                    } else {
                        db.insertarNoSocio(idCliente.toInt(), null, null)
                    }

                    if (resultado == -1L) {
                        Toast.makeText(this, "No se pudo completar el registro", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // 3. Todo salió bien
                    Toast.makeText(this, "$tipoCliente registrado correctamente", Toast.LENGTH_SHORT).show()
                    finish()


                }
            }
        }

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }

    }
}