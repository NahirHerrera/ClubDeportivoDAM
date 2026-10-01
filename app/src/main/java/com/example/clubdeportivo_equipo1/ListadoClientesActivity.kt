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

class ListadoClientesActivity : AppCompatActivity() {

    private lateinit var etBuscarDni: EditText
    private lateinit var tvTotalClientes: TextView
    private lateinit var contenedor: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_clientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etBuscarDni = findViewById(R.id.etBuscarDni)
        tvTotalClientes = findViewById(R.id.tvTotalClientes)
        contenedor = findViewById(R.id.contenedorClientes)

        findViewById<Button>(R.id.btnBuscar).setOnClickListener { mostrarClientes() }
        etBuscarDni.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mostrarClientes()
                true
            } else {
                false
            }
        }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        mostrarClientes()
    }

    private fun mostrarClientes() {
        val texto = etBuscarDni.text.toString().trim()
        val clientes = SociosRepository.buscarActivosPorDni(texto)

        tvTotalClientes.text = when {
            clientes.isEmpty() -> "No se encontraron clientes con ese documento"
            texto.isEmpty() -> "Clientes activos: ${clientes.size}"
            else -> "Resultados: ${clientes.size}"
        }

        contenedor.removeAllViews()
        for (socio in clientes) {
            val item = layoutInflater.inflate(R.layout.item_cliente, contenedor, false)
            item.findViewById<TextView>(R.id.tvIdCliente).text = "ID Cliente: ${socio.numeroFormateado}"
            item.findViewById<TextView>(R.id.tvTipoCliente).text = socio.tipoCliente
            item.findViewById<TextView>(R.id.tvNombre).text = socio.nombreCompleto
            item.findViewById<TextView>(R.id.tvDni).text = "DNI: ${socio.dniFormateado}"
            item.findViewById<TextView>(R.id.tvActividades).text = socio.actividades.joinToString(", ")
            item.findViewById<TextView>(R.id.tvFechaInscripcion).text =
                "Inscripción: ${socio.fechaInscripcion}"
            contenedor.addView(item)
        }
    }
}
