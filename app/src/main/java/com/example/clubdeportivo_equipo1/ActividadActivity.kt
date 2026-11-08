package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActividadActivity : AppCompatActivity() {
    private lateinit var dbHelper: ClubDBHelper
    private var clienteActual: Cliente? = null
    private var formaPagoElegida: String? = null
    private var factorPago: Double = 1.0
    private lateinit var adapter: ActividadAdapter

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
        dbHelper = ClubDBHelper(this)

        //Botón ir a pagar

        findViewById<Button>(R.id.btnIrAPagar).setOnClickListener {
            val vista = layoutInflater.inflate(R.layout.dialog_pago, null)

            val dialog = AlertDialog.Builder(this)
                .setView(vista)
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val rgFormaPago = vista.findViewById<RadioGroup>(R.id.rgFormaPago)

            vista.findViewById<Button>(R.id.btnPagar).setOnClickListener {
                if (!puedeAvanzar()) return@setOnClickListener
                when (rgFormaPago.checkedRadioButtonId) {
                    R.id.rbEfectivo -> { formaPagoElegida="Efectivo"; factorPago = 0.9 }
                    R.id.rbTarjeta3 -> { formaPagoElegida="Tarjeta: 3 Cuotas"; factorPago = 1.0 }
                    R.id.rbTarjeta6 -> { formaPagoElegida="Tarjeta: 6 Cuotas"; factorPago = 1.10 }
                    -1 -> Toast.makeText(this, "Elegí una forma de pago", Toast.LENGTH_SHORT).show()
                }
                if (rgFormaPago.checkedRadioButtonId != -1) {
                    Toast.makeText(this, "Forma de pago: $formaPagoElegida", Toast.LENGTH_SHORT).show()
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
                    val cliente = dbHelper.buscarCliente(documento)
                    clienteActual = cliente

                    if(cliente != null){
                        Toast.makeText(this, "Cliente encontrado: ${cliente.nombre} ${cliente.apellido}", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                    }
                    // documento válido: hacer la búsqueda

                }
            }

        }


        //Botón inscribir
        val rv = findViewById<RecyclerView>(R.id.rvActividades)
        adapter = ActividadAdapter(dbHelper.obtenerActividades())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        val btnInscribir = findViewById<Button>(R.id.btnInscribir)

        btnInscribir.setOnClickListener {
            if (!puedeAvanzar()) return@setOnClickListener
            val elegidas = adapter.getSeleccionadas()

            if (elegidas.isEmpty()) {
                Toast.makeText(this, "Seleccioná al menos una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (clienteActual == null) {
                Toast.makeText(this, "Primero buscá un cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (formaPagoElegida == null) {
                Toast.makeText(this, "Primero elegí la forma de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                val resultado = registrarInscripciones()
                if (!resultado) return@setOnClickListener
                finish()
            }


        }


        //Botón Volver
        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    //Una vez seleccionado el cliente, la forma de pago y las actividades, se registra la inscripción.
    private fun registrarInscripciones(): Boolean {
        val cliente = clienteActual ?: return false
        val formaPago = formaPagoElegida ?: return false
        val elegidas = adapter.getSeleccionadas()
        if (elegidas.isEmpty()) return false

        val fecha = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            .format(java.util.Date())

        var fallidas = 0
        for (a in elegidas) {
            val id = dbHelper.inscribirActividad(
                cliente.idCliente, a.IdActividad, formaPago, a.costo * factorPago, fecha
            )
            if (id == -1L) fallidas++
        }

        if (fallidas == 0) {
            Toast.makeText(this, "¡Inscripción realizada!", Toast.LENGTH_SHORT).show()
            formaPagoElegida = null
            factorPago = 1.0

        } else {
            Toast.makeText(this, "No se pudo inscribir a $fallidas actividad(es) (¿ya inscripto?)",
                Toast.LENGTH_LONG).show()
        }

        return fallidas == 0
    }

    //Función que verifica si el cliente ya está inscripto en alguna de las actividades seleccionadas.
    private fun puedeAvanzar(): Boolean {
        val cliente = clienteActual
        if (cliente == null) {
            Toast.makeText(this, "Primero buscá un cliente", Toast.LENGTH_SHORT).show()
            return false
        }

        val elegidas = adapter.getSeleccionadas()
        if (elegidas.isEmpty()) {
            Toast.makeText(this, "Seleccioná al menos una actividad", Toast.LENGTH_SHORT).show()
            return false
        }

        val inscriptas = dbHelper.actividadesInscriptas(cliente.idCliente)
        val repetidas = elegidas.filter { it.IdActividad in inscriptas }

        if (repetidas.isNotEmpty()) {
            Toast.makeText(
                this,
                "Ya está inscripto en: ${repetidas.joinToString(", ") { it.nombre }}. Deseleccionalas para continuar.",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        return true
    }

}