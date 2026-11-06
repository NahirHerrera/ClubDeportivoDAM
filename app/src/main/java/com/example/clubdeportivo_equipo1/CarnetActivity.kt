package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CarnetActivity : AppCompatActivity() {

    companion object {
        // Contenido del QR del carnet: prefijo del club + DNI del socio
        private const val PREFIJO_QR = "CLUBDEPORTIVO-DNI:"

        fun contenidoQr(dni: String): String = PREFIJO_QR + dni

        // Devuelve el DNI si el QR es un carnet del club, o null si es cualquier otro QR
        fun dniDesdeQr(contenido: String): String? {
            if (!contenido.startsWith(PREFIJO_QR)) return null
            val dni = contenido.removePrefix(PREFIJO_QR)
            return if (dni.isNotEmpty() && dni.all { it.isDigit() }) dni else null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        // El DNI llega desde Cobrar Cuota (botón Mostrar Carnet o Escanear Carnet)
        val dni = intent.getStringExtra(CobrarCuotaActivity.EXTRA_DNI_SOCIO)
        val db = ClubDBHelper(this)
        val socio = dni?.let { db.buscarSocioCuota(it) }
        val fechaAptoFisico = dni?.let { db.fechaAptoFisico(it) }
        db.close()

        if (socio == null || !socio.esSocio) {
            Toast.makeText(this, "No se encontró el socio", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.Nombre).text = socio.nombreCompleto.uppercase()
        findViewById<TextView>(R.id.Socio).text = "N° Socio: ${socio.nroSocioFormateado}"
        findViewById<TextView>(R.id.Dni).text = "DNI: ${socio.dniFormateado}"
        findViewById<TextView>(R.id.Vigencia).text =
            if (socio.cuotaAlDia) "Vigencia: ACTIVO" else "Vigencia: INACTIVO"

        mostrarAptoFisico(fechaAptoFisico)

        // QR con el DNI del socio, para escanearlo desde Cobrar Cuota
        val matrix = MultiFormatWriter().encode(
            contenidoQr(socio.dni),
            BarcodeFormat.QR_CODE,
            300,
            300
        )
        findViewById<ImageView>(R.id.img_qr).setImageBitmap(BarcodeEncoder().createBitmap(matrix))

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }

    private fun mostrarAptoFisico(fechaApto: String?) {
        val tvApto = findViewById<TextView>(R.id.apto_fisico)
        val tvPresentado = findViewById<TextView>(R.id.apto_fisico_vencimiento)

        if (fechaApto == null) {
            tvApto.text = "APTO FISICO: SIN DATOS"
            tvPresentado.text = "Sin fecha de presentación"
            return
        }

        tvApto.text = if (aptoVigente(fechaApto)) "APTO FISICO: VIGENTE" else "APTO FISICO: VENCIDO"
        tvPresentado.text = "Presentado: ${fechaApto.split("-").reversed().joinToString("/")}"
    }

    private fun aptoVigente(fechaPresentacion: String): Boolean {
        val fecha = runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fechaPresentacion)
        }.getOrNull() ?: return false
        val vence = Calendar.getInstance().apply {
            time = fecha
            add(Calendar.YEAR, 1)
        }
        return vence.after(Calendar.getInstance())
    }
}
