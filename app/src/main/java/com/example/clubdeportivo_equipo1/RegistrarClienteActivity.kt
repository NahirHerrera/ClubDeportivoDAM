package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarClienteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etTipoCliente: EditText = findViewById(R.id.TipoCliente)
        val etNombre: EditText = findViewById(R.id.Nombre)
        val etApellido: EditText = findViewById(R.id.Apellido)

        val soloTextoFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.isEmpty()) return@InputFilter null
            val pattern = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
            if (pattern.matches(source)) null else ""
        }

        etTipoCliente.filters = arrayOf(soloTextoFilter)
        etNombre.filters = arrayOf(soloTextoFilter)
        etApellido.filters = arrayOf(soloTextoFilter)

        val spinner: Spinner = findViewById(R.id.tipoSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.tipoRegistraCliente,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        findViewById<Button>(R.id.btnRegistrarCliente).setOnClickListener {
            val tipoCliente = findViewById<EditText>(R.id.TipoCliente).text.toString().trim()
            val nombre = findViewById<EditText>(R.id.Nombre).text.toString()
            val apellido = findViewById<EditText>(R.id.Apellido).text.toString().trim()
            val documento = findViewById<EditText>(R.id.Documento).text.toString().trim()
            val tipoSeleccionado = findViewById<Spinner>(R.id.tipoSpinner).selectedItem?.toString() ?: ""

            if (tipoCliente.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() || tipoSeleccionado.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos para registrar", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, HomeActivity::class.java))
                Toast.makeText(this, "Cliente registrado correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}