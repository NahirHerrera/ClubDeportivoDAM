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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.NumberFormat
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

    private lateinit var db: ClubDBHelper
    private var socioActual: SocioCuota? = null
    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    // Escáner del QR del carnet (pide el permiso de cámara la primera vez)
    private val escanerQr = registerForActivityResult(ScanContract()) { resultado ->
        val contenido = resultado.contents
        if (contenido == null) {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
        } else {
            procesarQr(contenido)
        }
    }

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

        db = ClubDBHelper(this)
        db.cargarDatosPruebaCobrarCuota()

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
        findViewById<Button>(R.id.btnEscanearCarnet).setOnClickListener { escanearCarnet() }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
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
        if (!dni.all { it.isDigit() }) {
            etDni.error = "Ingresá solo números"
            return
        }

        val socio = db.buscarSocioCuota(dni)
        if (socio == null) {
            Toast.makeText(this, "No se encontró un cliente con DNI $dni", Toast.LENGTH_SHORT).show()
            return
        }

        socioActual = socio
        tvNombreSocio.text = socio.nombreCompleto
        panelSocio.visibility = View.VISIBLE

        if (!socio.esSocio) {
            tvEstadoCuota.text = "No socio"
            tvDatosSocio.text = "DNI: ${socio.dniFormateado}"
            AlertDialog.Builder(this)
                .setTitle("Cliente no socio")
                .setMessage("${socio.nombreCompleto} no es socio: no paga cuota mensual, abona por actividad.")
                .setPositiveButton("Aceptar", null)
                .show()
            return
        }

        sincronizarMemoria(socio.dni, socio.cuotaAlDia)

        if (socio.cuotaAlDia) {
            tvEstadoCuota.text = "Al día"
            tvDatosSocio.text = "N° Socio: ${socio.nroSocioFormateado}  •  DNI: ${socio.dniFormateado}" +
                "\nVence: ${socio.vencimientoFormateado}"
            AlertDialog.Builder(this)
                .setTitle("Cuota al día")
                .setMessage("Este socio tiene la cuota al dia")
                .setPositiveButton("Aceptar", null)
                .show()
        } else {
            tvEstadoCuota.text = "Debe cuota"
            tvDatosSocio.text = "N° Socio: ${socio.nroSocioFormateado}  •  DNI: ${socio.dniFormateado}" +
                "\nVenció: ${socio.vencimientoFormateado}"
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
        if (!socio.esSocio) {
            Toast.makeText(this, "Los clientes no socios no pagan cuota mensual", Toast.LENGTH_SHORT).show()
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

    private fun registrarPago(socio: SocioCuota, medioPago: String, monto: Double) {
        val pago = try {
            db.registrarPagoCuota(socio.idCliente, SociosRepository.VALOR_CUOTA, monto)
        } catch (e: Exception) {
            null
        }
        if (pago == null) {
            AlertDialog.Builder(this)
                .setTitle("No se pudo registrar el pago")
                .setMessage("Ocurrió un error al guardar el pago. No se cobró nada, intentá de nuevo.")
                .setPositiveButton("Aceptar", null)
                .show()
            return
        }

        val socioPagado = socio.copy(fechaVencimiento = pago.nuevoVencimiento, cuotaAlDia = true)
        socioActual = socioPagado
        sincronizarMemoria(socio.dni, true)

        tvEstadoCuota.text = "Al día"
        tvDatosSocio.text = "N° Socio: ${socioPagado.nroSocioFormateado}  •  DNI: ${socioPagado.dniFormateado}" +
            "\nVence: ${socioPagado.vencimientoFormateado}"
        panelPago.visibility = View.GONE

        AlertDialog.Builder(this)
            .setTitle("Pago registrado")
            .setMessage(
                "La cuota de ${socio.nombreCompleto} fue abonada correctamente.\n" +
                    "Próximo vencimiento: ${socioPagado.vencimientoFormateado}"
            )
            .setCancelable(false)
            .setPositiveButton("Ver Comprobante") { _, _ ->
                mostrarComprobante(socioPagado, medioPago, monto, pago)
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun escanearCarnet() {
        val opciones = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Apuntá la cámara al QR del carnet")
            setBeepEnabled(true)
        }
        escanerQr.launch(opciones)
    }

    // Si el QR es un carnet del club, carga al socio en la pantalla y abre su carnet
    private fun procesarQr(contenido: String) {
        val dni = CarnetActivity.dniDesdeQr(contenido)
        if (dni == null) {
            Toast.makeText(this, "El QR no es un carnet del club", Toast.LENGTH_SHORT).show()
            return
        }

        val socio = db.buscarSocioCuota(dni)
        if (socio == null || !socio.esSocio) {
            Toast.makeText(this, "El carnet no corresponde a ningún socio", Toast.LENGTH_SHORT).show()
            return
        }

        // Deja al socio cargado en Cobrar Cuota, por si al volver del carnet hay que cobrarle
        etDni.setText(dni)
        socioActual = socio
        tvNombreSocio.text = socio.nombreCompleto
        tvEstadoCuota.text = if (socio.cuotaAlDia) "Al día" else "Debe cuota"
        tvDatosSocio.text = "N° Socio: ${socio.nroSocioFormateado}  •  DNI: ${socio.dniFormateado}" +
            if (socio.cuotaAlDia) "\nVence: ${socio.vencimientoFormateado}" else "\nVenció: ${socio.vencimientoFormateado}"
        panelSocio.visibility = View.VISIBLE
        rgMedioPago.clearCheck()
        panelPago.visibility = if (socio.cuotaAlDia) View.GONE else View.VISIBLE
        sincronizarMemoria(socio.dni, socio.cuotaAlDia)

        mostrarCarnet(socio)
    }

    // Listado de Deudores todavía lee los socios de SociosRepository (memoria).
    // Hasta que pase a la base, se le copia el estado de la cuota para que no quede desfasado.
    private fun sincronizarMemoria(dni: String, cuotaAlDia: Boolean) {
        SociosRepository.buscarPorDni(dni)?.cuotaAlDia = cuotaAlDia
    }

    private fun onMostrarCarnet() {
        val socio = socioActual
        if (socio == null) {
            Toast.makeText(this, "Primero buscá un socio por DNI", Toast.LENGTH_SHORT).show()
            return
        }
        if (!socio.esSocio) {
            Toast.makeText(this, "Solo los socios tienen carnet", Toast.LENGTH_SHORT).show()
            return
        }
        if (!socio.cuotaAlDia) {
            Toast.makeText(this, "El socio debe tener la cuota al día para ver el carnet", Toast.LENGTH_SHORT).show()
            return
        }
        mostrarCarnet(socio)
    }

    // Comprobante de pago de la cuota: abre ComprobanteActivity con los datos del pago
    private fun mostrarComprobante(socio: SocioCuota, medioPago: String, monto: Double, pago: PagoCuota) {
        val intent = Intent(this, ComprobanteActivity::class.java).apply {
            putExtra(ComprobanteActivity.EXTRA_NOMBRE, socio.nombre.uppercase())
            putExtra(ComprobanteActivity.EXTRA_APELLIDO, socio.apellido.uppercase())
            putExtra(ComprobanteActivity.EXTRA_NUMERO_SOCIO, socio.nroSocioFormateado)
            putExtra(ComprobanteActivity.EXTRA_DNI, socio.dniFormateado)
            putExtra(ComprobanteActivity.EXTRA_MEDIO_PAGO, medioPago)
            putExtra(ComprobanteActivity.EXTRA_MONTO, monto)
            putExtra(ComprobanteActivity.EXTRA_NUMERO_COMPROBANTE, pago.nroComprobante)
            putExtra(ComprobanteActivity.EXTRA_FECHA, formatearFecha(pago.fechaPago))
        }
        startActivity(intent)
    }

    // Abre el carnet (CarnetActivity) y le pasa el DNI del socio en EXTRA_DNI_SOCIO
    private fun mostrarCarnet(socio: SocioCuota) {
        val intent = Intent(this, CarnetActivity::class.java)
        intent.putExtra(EXTRA_DNI_SOCIO, socio.dni)
        startActivity(intent)
    }
}
