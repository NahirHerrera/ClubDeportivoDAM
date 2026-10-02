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
    private lateinit var tvMensaje: TextView
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
        tvMensaje = findViewById(R.id.tvMensaje)
        contenedor = findViewById(R.id.contenedorClientes)

        findViewById<Button>(R.id.btnBuscar).setOnClickListener { buscarCliente() }
        etBuscarDni.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarCliente()
                true
            } else {
                false
            }
        }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }

    private fun buscarCliente() {
        val dni = etBuscarDni.text.toString().trim()
        contenedor.removeAllViews()

        if (dni.isEmpty()) {
            etBuscarDni.error = "Ingresá un DNI"
            tvMensaje.text = "Ingresá el DNI del cliente para ver sus actividades"
            return
        }

        val socio = SociosRepository.buscarPorDni(dni)
        tvMensaje.text = when {
            socio == null -> "No existe un cliente con DNI $dni"
            !socio.activo -> "El cliente con DNI $dni no está activo"
            // Sin la cuota paga no figura en el listado de clientes
            !socio.cuotaAlDia -> "El cliente con DNI $dni no tiene la cuota al día, por eso no figura en el listado"
            else -> ""
        }
        if (socio == null || !socio.activo || !socio.cuotaAlDia) return

        val item = layoutInflater.inflate(R.layout.item_cliente, contenedor, false)
        item.findViewById<TextView>(R.id.tvIdCliente).text = "ID Cliente: ${socio.numeroFormateado}"
        item.findViewById<TextView>(R.id.tvTipoCliente).text = socio.tipoCliente
        item.findViewById<TextView>(R.id.tvNombre).text = socio.nombreCompleto
        item.findViewById<TextView>(R.id.tvDni).text = "DNI: ${socio.dniFormateado}"
        item.findViewById<TextView>(R.id.tvCuota).text = "Cuota: Al día"
        item.findViewById<TextView>(R.id.tvFechaInscripcion).text =
            "Fecha de inscripción: ${socio.fechaInscripcion}"
        item.findViewById<TextView>(R.id.tvActividades).text =
            if (socio.actividades.isEmpty()) "Sin actividades inscriptas"
            else socio.actividades.joinToString("\n") { "• $it" }
        contenedor.addView(item)
    }
}
