package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class ComprobanteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_APELLIDO = "extra_apellido"
        const val EXTRA_NUMERO_SOCIO = "extra_numero_socio"
        const val EXTRA_DNI = "extra_dni"
        const val EXTRA_MEDIO_PAGO = "extra_medio_pago"
        const val EXTRA_MONTO = "extra_monto"
        const val EXTRA_NUMERO_COMPROBANTE = "extra_numero_comprobante"
        const val EXTRA_FECHA = "extra_fecha"
    }

    private val formatoMoneda =
        NumberFormat.getCurrencyInstance(Locale("es", "AR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante)

        val nombre = intent.getStringExtra(EXTRA_NOMBRE) ?: ""
        val apellido = intent.getStringExtra(EXTRA_APELLIDO) ?: ""
        val numeroSocio = intent.getStringExtra(EXTRA_NUMERO_SOCIO) ?: ""
        val dni = intent.getStringExtra(EXTRA_DNI) ?: ""
        val medioPago = intent.getStringExtra(EXTRA_MEDIO_PAGO) ?: ""
        val monto = intent.getDoubleExtra(EXTRA_MONTO, 0.0)
        val numeroComprobante =
            intent.getIntExtra(EXTRA_NUMERO_COMPROBANTE, 0)
        val fecha = intent.getStringExtra(EXTRA_FECHA) ?: ""

        // Los rótulos ("N° Comprobante:", "Nombre:", etc.) ya están en el layout
        findViewById<TextView>(R.id.tvNumeroComprobante).text =
            numeroComprobante.toString()

        findViewById<TextView>(R.id.tvFecha).text = fecha

        findViewById<TextView>(R.id.tvNroSocio).text = numeroSocio

        findViewById<TextView>(R.id.tvNombre).text = nombre

        findViewById<TextView>(R.id.tvApellido).text = apellido

        findViewById<TextView>(R.id.tvDni).text = dni

        findViewById<TextView>(R.id.tvConcepto).text = "Cuota mensual"

        findViewById<TextView>(R.id.tvMedioPago).text = medioPago

        findViewById<TextView>(R.id.tvTotal).text =
            formatoMoneda.format(monto)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }
}