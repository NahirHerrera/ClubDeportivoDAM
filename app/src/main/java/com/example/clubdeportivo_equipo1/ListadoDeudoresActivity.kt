package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale

class ListadoDeudoresActivity : AppCompatActivity() {

    private lateinit var etBuscarDni: EditText
    private lateinit var tvMensaje: TextView
    private lateinit var contenedor: LinearLayout

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_deudores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etBuscarDni = findViewById(R.id.etBuscarDni)
        tvMensaje = findViewById(R.id.tvMensaje)
        contenedor = findViewById(R.id.contenedorDeudores)

        findViewById<Button>(R.id.btnBuscar).setOnClickListener { mostrarDeudores() }
        etBuscarDni.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mostrarDeudores()
                true
            } else {
                false
            }
        }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }

    // Se refresca al volver a la pantalla (por si se cobró una cuota mientras tanto)
    override fun onResume() {
        super.onResume()
        mostrarDeudores()
    }

    private fun mostrarDeudores() {
        val dni = etBuscarDni.text.toString().trim()
        val deudores = SociosRepository.deudores()
        // Con el campo vacío se muestran todos; si no, se filtra por DNI
        val lista = if (dni.isEmpty()) deudores else deudores.filter { it.dni.startsWith(dni) }

        contenedor.removeAllViews()

        tvMensaje.text = when {
            deudores.isEmpty() -> "No hay socios con la cuota pendiente"
            lista.isEmpty() -> "Ningún deudor coincide con el DNI $dni"
            dni.isEmpty() -> {
                val total = deudores.size * SociosRepository.VALOR_CUOTA
                val cantidad = if (deudores.size == 1) "1 socio debe la cuota"
                else "${deudores.size} socios deben la cuota"
                "$cantidad • Total adeudado: ${formatoMoneda.format(total)}"
            }
            else -> "Mostrando ${lista.size} de ${deudores.size} deudores"
        }

        lista.forEach { socio ->
            val item = layoutInflater.inflate(R.layout.item_deudor, contenedor, false)
            item.findViewById<TextView>(R.id.tvIdCliente).text = "ID Cliente: ${socio.numeroFormateado}"
            item.findViewById<TextView>(R.id.tvNombre).text = socio.nombreCompleto
            item.findViewById<TextView>(R.id.tvDni).text = "DNI: ${socio.dniFormateado}"
            item.findViewById<TextView>(R.id.tvFechaInscripcion).text =
                "Fecha de inscripción: ${socio.fechaInscripcion}"
            item.findViewById<TextView>(R.id.tvMonto).text =
                formatoMoneda.format(SociosRepository.VALOR_CUOTA)
            contenedor.addView(item)
        }
    }
}