package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
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

    companion object {
        const val EXTRA_DNI_SOCIO = "dni_socio"
    }

    private lateinit var etDni: EditText
    private lateinit var panelSocio: LinearLayout
    private lateinit var tvNombreSocio: TextView
    private lateinit var tvEstadoCuota: TextView
    private lateinit var tvDatosSocio: TextView
    private lateinit var panelPago: LinearLayout
    private lateinit var rgMedioPago: RadioGroup

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
        tvEstadoCuota = findViewById(R.id.tvEstadoCuota)
        tvDatosSocio = findViewById(R.id.tvDatosSocio)
        panelPago = findViewById(R.id.panelPago)
        rgMedioPago = findViewById(R.id.rgMedioPago)

        val cuota = SociosRepository.VALOR_CUOTA
        findViewById<RadioButton>(R.id.rbEfectivo).text =
            "Efectivo (10% de Descuento)\n${formatoMoneda.format(SociosRepository.montoEfectivo())}"
        findViewById<RadioButton>(R.id.rb3Cuotas).text =
            "Tarjeta de Crédito:\n3 Cuotas Sin Interés de ${formatoMoneda.format(cuota / 3)}"
        findViewById<RadioButton>(R.id.rb6Cuotas).text =
            "Tarjeta de Crédito:\n6 Cuotas Sin Interés de ${formatoMoneda.format(cuota / 6)}"

        findViewById<Button>(R.id.btnBuscar).setOnClickListener { buscarSocio() }
        etDni.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarSocio()
                true
            } else {
                false
            }
        }

        findViewById<Button>(R.id.btnPagarCuota).setOnClickListener { pagarCuota() }
        findViewById<Button>(R.id.btnMostrarCarnet).setOnClickListener { onMostrarCarnet() }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }

    private fun buscarSocio() {
        val dni = etDni.text.toString().trim()
        socioActual = null
        panelSocio.visibility = View.GONE
        panelPago.visibility = View.GONE

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
        tvNombreSocio.text = socio.nombreCompleto
        tvDatosSocio.text = "N° Socio: ${socio.numeroFormateado}  •  DNI: ${socio.dniFormateado}"
        panelSocio.visibility = View.VISIBLE

        if (socio.cuotaAlDia) {
            tvEstadoCuota.text = "Al día"
            AlertDialog.Builder(this)
                .setTitle("Cuota al día")
                .setMessage("Este socio tiene la cuota al dia")
                .setPositiveButton("Aceptar", null)
                .show()
        } else {
            tvEstadoCuota.text = "Debe cuota"
            rgMedioPago.clearCheck()
            panelPago.visibility = View.VISIBLE
        }
    }

    private fun pagarCuota() {
        val socio = socioActual
        if (socio == null) {
            Toast.makeText(this, "Primero buscá un socio por DNI", Toast.LENGTH_SHORT).show()
            return
        }
        if (socio.cuotaAlDia) {
            Toast.makeText(this, "Este socio tiene la cuota al dia", Toast.LENGTH_SHORT).show()
            return
        }

        val cuota = SociosRepository.VALOR_CUOTA
        val (medioPago, monto) = when (rgMedioPago.checkedRadioButtonId) {
            R.id.rbEfectivo -> "Efectivo (10% de descuento)" to SociosRepository.montoEfectivo()
            R.id.rb3Cuotas -> "Tarjeta de Crédito - 3 cuotas sin interés" to cuota
            R.id.rb6Cuotas -> "Tarjeta de Crédito - 6 cuotas sin interés" to cuota
            else -> {
                Toast.makeText(this, "Seleccioná una forma de pago", Toast.LENGTH_SHORT).show()
                return
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmar pago")
            .setMessage("¿Registrar el pago de ${formatoMoneda.format(monto)} de ${socio.nombreCompleto} con $medioPago?")
            .setPositiveButton("Pagar") { _, _ -> registrarPago(socio, medioPago, monto) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun registrarPago(socio: Socio, medioPago: String, monto: Double) {
        val nroComprobante = SociosRepository.registrarPago(socio)

        tvEstadoCuota.text = "Al día"
        panelPago.visibility = View.GONE

        AlertDialog.Builder(this)
            .setTitle("Pago registrado")
            .setMessage("La cuota de ${socio.nombreCompleto} fue abonada correctamente.")
            .setCancelable(false)
            .setPositiveButton("Ver Comprobante") { _, _ ->
                mostrarComprobante(socio, medioPago, monto, nroComprobante)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun onMostrarCarnet() {
        val socio = socioActual
        if (socio == null) {
            Toast.makeText(this, "Primero buscá un socio por DNI", Toast.LENGTH_SHORT).show()
            return
        }
        if (!socio.cuotaAlDia) {
            Toast.makeText(this, "El socio debe tener la cuota al día para ver el carnet", Toast.LENGTH_SHORT).show()
            return
        }
        mostrarCarnet(socio)
    }

    // Comprobante de pago de la cuota: abre ComprobanteActivity con los datos del pago
    private fun mostrarComprobante(socio: Socio, medioPago: String, monto: Double, nroComprobante: Int) {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val intent = Intent(this, ComprobanteActivity::class.java).apply {
            putExtra(ComprobanteActivity.EXTRA_NOMBRE, socio.nombre.uppercase())
            putExtra(ComprobanteActivity.EXTRA_APELLIDO, socio.apellido.uppercase())
            putExtra(ComprobanteActivity.EXTRA_NUMERO_SOCIO, socio.numeroFormateado)
            putExtra(ComprobanteActivity.EXTRA_DNI, socio.dniFormateado)
            putExtra(ComprobanteActivity.EXTRA_MEDIO_PAGO, medioPago)
            putExtra(ComprobanteActivity.EXTRA_MONTO, monto)
            putExtra(ComprobanteActivity.EXTRA_NUMERO_COMPROBANTE, nroComprobante)
            putExtra(ComprobanteActivity.EXTRA_FECHA, fecha)
        }
        startActivity(intent)
    }

    // ENDPOINT ABIERTO - Carnet del socio.
    // TODO: conectar con el carnet que está desarrollando el compañero.
    // Por ahora abre CarnetActivity y le pasa el DNI del socio en EXTRA_DNI_SOCIO.
    private fun mostrarCarnet(socio: Socio) {
        val intent = Intent(this, CarnetActivity::class.java)
        intent.putExtra(EXTRA_DNI_SOCIO, socio.dni)
        startActivity(intent)
    }
}
