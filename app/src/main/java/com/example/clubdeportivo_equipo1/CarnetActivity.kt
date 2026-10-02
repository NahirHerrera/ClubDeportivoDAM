package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class CarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        // Si llega el DNI de un socio (desde Cobrar Cuota), se cargan sus datos
        val dni = intent.getStringExtra(CobrarCuotaActivity.EXTRA_DNI_SOCIO)
        val socio = dni?.let { SociosRepository.buscarPorDni(it) }
        if (socio != null) {
            findViewById<TextView>(R.id.Nombre).text = socio.nombreCompleto.uppercase()
            findViewById<TextView>(R.id.Socio).text = "N° Socio: ${socio.numeroFormateado}"
            findViewById<TextView>(R.id.Dni).text = "DNI: ${socio.dniFormateado}"
            findViewById<TextView>(R.id.Vigencia).text =
                if (socio.activo && socio.cuotaAlDia) "Vigencia: ACTIVO" else "Vigencia: INACTIVO"

            val vigente = aptoVigente(socio.aptoFisicoFecha)
            findViewById<TextView>(R.id.apto_fisico).text =
                if (vigente) "APTO FISICO: VIGENTE" else "APTO FISICO: VENCIDO"
            findViewById<TextView>(R.id.apto_fisico_vencimiento).text =
                "Presentado: ${socio.aptoFisicoFecha}"
        }

        //QR
        val img_qr = findViewById<ImageView>(R.id.img_qr)
        val numeroSocio = "0001"
        val writer = MultiFormatWriter()
        val matrix = writer.encode(
            numeroSocio,
            BarcodeFormat.QR_CODE,
            300,
            300
        )
        val encoder = BarcodeEncoder()
        val bitmap = encoder.createBitmap(matrix)

        img_qr.setImageBitmap(bitmap)

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }
    private fun aptoVigente(fechaInscripcion: String): Boolean {
        val vence = Calendar.getInstance().apply {
            time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaInscripcion)!!
            add(Calendar.YEAR, 1)
        }
        return vence.after(Calendar.getInstance())
    }
}
