package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActividadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etDocumento = findViewById<EditText>(R.id.etDocumento)

        //Botón ir a pagar

        findViewById<Button>(R.id.btnIrAPagar).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_pago, null)

            val dialog = AlertDialog.Builder(this)
                .setView(vista)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val rgFormaPago = vista.findViewById<RadioGroup>(R.id.rgFormaPago)

            vista.findViewById<Button>(R.id.btnPagar).setOnClickListener {
                when (rgFormaPago.checkedRadioButtonId) {
                    R.id.rbEfectivo -> { /* pago con 10% de descuento */ }
                    R.id.rbTarjeta3 -> { /* 3 cuotas */ }
                    R.id.rbTarjeta6 -> { /* 6 cuotas */ }
                    -1 -> Toast.makeText(this, "Elegí una forma de pago", Toast.LENGTH_SHORT).show()
                }
                if (rgFormaPago.checkedRadioButtonId != -1) {
                    Toast.makeText(this, "Pago realizado", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            vista.findViewById<Button>(R.id.btnCancelarPago).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }


        //Botón Buscar
        findViewById<Button>(R.id.btnBuscarActividad).setOnClickListener {
            val documento = etDocumento.text.toString().trim()

            when {
                documento.isEmpty() -> {
                    etDocumento.error = "Ingresá el documento"
                }

                documento.length < 6 -> {
                    etDocumento.error = "Debe tener al menos 6 dígitos"
                }

                !documento.all { it.isDigit() } ->{
                    etDocumento.error = "Ingrese solo números"
                }

                else -> {
                    Toast.makeText(this, "Buscando persona...", Toast.LENGTH_SHORT).show()
                    // documento válido: hacer la búsqueda
                }
            }
        }




        //Tarjetas de actividades botón Inscribir
        val tarjetas = listOf(
            findViewById<View>(R.id.tarjeta1),
            findViewById<View>(R.id.tarjeta2),
            findViewById<View>(R.id.tarjeta3),
            findViewById<View>(R.id.tarjeta4),
            findViewById<View>(R.id.tarjeta5)
        )

        val btnInscribir = findViewById<Button>(R.id.btnInscribir)

        // Pintar/despintar al tocar una tarjeta
        tarjetas.forEach { tarjeta ->
            tarjeta.setOnClickListener { it.isSelected = !it.isSelected }
        }

        // Validación al tocar "Inscribir"
        btnInscribir.setOnClickListener {
            if (tarjetas.none { it.isSelected }) {
                Toast.makeText(this, "Seleccioná al menos una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                Toast.makeText(this, "¡Inscripción realizada!", Toast.LENGTH_SHORT).show()
            }

            // Hay al menos una actividad seleccionada: continuar con la inscripción
        }


        //Botón Volver
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}