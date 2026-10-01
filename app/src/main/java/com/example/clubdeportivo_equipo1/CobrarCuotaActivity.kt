package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CobrarCuotaActivity : AppCompatActivity() {

    private lateinit var etDni: EditText
    private lateinit var panelSocio: LinearLayout
    private lateinit var tvNombreSocio: TextView
    private lateinit var tvNumeroSocio: TextView
    private lateinit var tvDniSocio: TextView
    private lateinit var tvEstadoCuota: TextView
    private lateinit var panelPago: LinearLayout
    private lateinit var panelComprobante: LinearLayout
    private lateinit var tvComprobante: TextView
    private lateinit var btnVerCarnet: Button

    private var socioActual: Socio? = null
    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etDni = findViewById(R.id.etDni)
        panelSocio = findViewById(R.id.panelSocio)
        tvNombreSocio = findViewById(R.id.tvNombreSocio)
        tvNumeroSocio = findViewById(R.id.tvNumeroSocio)
        tvDniSocio = findViewById(R.id.tvDniSocio)
        tvEstadoCuota = findViewById(R.id.tvEstadoCuota)
        panelPago = findViewById(R.id.panelPago)
        panelComprobante = findViewById(R.id.panelComprobante)
        tvComprobante = findViewById(R.id.tvComprobante)
        btnVerCarnet = findViewById(R.id.btnVerCarnet)

        val cuota = SociosRepository.VALOR_CUOTA
        val textoEfectivo = "Efectivo"
        val texto3Cuotas = "3 cuotas sin interés de ${formatoMoneda.format(cuota / 3)}"
        val texto6Cuotas = "6 cuotas sin interés de ${formatoMoneda.format(cuota / 6)}"

        findViewById<TextView>(R.id.tvMontoCuota).text = "Cuota mensual: ${formatoMoneda.format(cuota)}"

        val btnEfectivo = findViewById<Button>(R.id.btnEfectivo)
        val btn3Cuotas = findViewById<Button>(R.id.btn3Cuotas)
        val btn6Cuotas = findViewById<Button>(R.id.btn6Cuotas)
        btnEfectivo.text = textoEfectivo
        btn3Cuotas.text = texto3Cuotas
        btn6Cuotas.text = texto6Cuotas

        btnEfectivo.setOnClickListener { confirmarPago(textoEfectivo) }
        btn3Cuotas.setOnClickListener { confirmarPago(texto3Cuotas) }
        btn6Cuotas.setOnClickListener { confirmarPago(texto6Cuotas) }

        findViewById<Button>(R.id.btnBuscar).setOnClickListener { buscarSocio() }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        btnVerCarnet.setOnClickListener {
            socioActual?.let { socio ->
                val intent = Intent(this, CarnetActivity::class.java)
                intent.putExtra(CarnetActivity.EXTRA_DNI, socio.dni)
                startActivity(intent)
            }
        }
    }

    private fun buscarSocio() {
        val dni = etDni.text.toString().trim()
        ocultarResultados()

        if (dni.isEmpty()) {
            etDni.error = "Ingresá un DNI"
            return
        }

        val socio = SociosRepository.buscarPorDni(dni)
        if (socio == null) {
            Toast.makeText(this, "No se encontró un socio con DNI $dni", Toast.LENGTH_SHORT).show()
            return
        }

        socioActual = socio
        tvNombreSocio.text = socio.nombreCompleto.uppercase()
        tvNumeroSocio.text = "N° Socio: ${socio.numeroFormateado}"
        tvDniSocio.text = "DNI: ${socio.dniFormateado}"
        panelSocio.visibility = View.VISIBLE

        if (socio.cuotaAlDia) {
            tvEstadoCuota.text = "CUOTA AL DÍA"
            AlertDialog.Builder(this)
                .setTitle("Cuota al día")
                .setMessage("Este socio tiene la cuota al dia")
                .setPositiveButton("Aceptar", null)
                .show()
        } else {
            tvEstadoCuota.text = "CUOTA PENDIENTE DE PAGO"
            panelPago.visibility = View.VISIBLE
        }
    }

    private fun confirmarPago(medioPago: String) {
        val socio = socioActual ?: return
        AlertDialog.Builder(this)
            .setTitle("Confirmar pago")
            .setMessage("¿Registrar el pago de la cuota de ${socio.nombreCompleto} en $medioPago?")
            .setPositiveButton("Abonar") { _, _ -> abonarCuota(socio, medioPago) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abonarCuota(socio: Socio, medioPago: String) {
        val nroComprobante = SociosRepository.registrarPago(socio)
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        tvEstadoCuota.text = "CUOTA AL DÍA"
        panelPago.visibility = View.GONE

        tvComprobante.text = """
            Comprobante N°: $nroComprobante
            Fecha: $fecha
            Socio: ${socio.nombreCompleto}
            N° Socio: ${socio.numeroFormateado}
            DNI: ${socio.dniFormateado}
            Concepto: Cuota mensual
            Medio de pago: $medioPago
            Total abonado: ${formatoMoneda.format(SociosRepository.VALOR_CUOTA)}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Pago registrado")
            .setMessage("La cuota de ${socio.nombreCompleto} fue abonada correctamente.")
            .setCancelable(false)
            .setPositiveButton("Ver comprobante") { _, _ ->
                panelComprobante.visibility = View.VISIBLE
                btnVerCarnet.visibility = View.VISIBLE
            }
            .show()
    }

    private fun ocultarResultados() {
        socioActual = null
        panelSocio.visibility = View.GONE
        panelPago.visibility = View.GONE
        panelComprobante.visibility = View.GONE
        btnVerCarnet.visibility = View.GONE
    }
}
