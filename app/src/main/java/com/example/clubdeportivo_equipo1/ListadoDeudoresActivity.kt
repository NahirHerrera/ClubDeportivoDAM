package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ListadoDeudoresActivity : AppCompatActivity() {

    private lateinit var tvTotalDeudores: TextView
    private lateinit var contenedorDeudores: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_deudores)

        tvTotalDeudores = findViewById(R.id.tvTotalDeudores)
        contenedorDeudores = findViewById(R.id.contenedorDeudores)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        mostrarDeudores()
    }

    private fun mostrarDeudores() {
        val deudores = SociosRepository.listarDeudores()

        tvTotalDeudores.text =
            "Socios con cuota pendiente: ${deudores.size}"

        contenedorDeudores.removeAllViews()

        for (socio in deudores) {

            val item = layoutInflater.inflate(
                R.layout.item_deudor,
                contenedorDeudores,
                false
            )

            item.findViewById<TextView>(R.id.tvNombreDeudor).text =
                socio.nombreCompleto.uppercase()

            item.findViewById<TextView>(R.id.tvNumeroSocioDeudor).text =
                "N° Socio: ${socio.numeroFormateado}"

            item.findViewById<TextView>(R.id.tvDniDeudor).text =
                "DNI: ${socio.dniFormateado}"

            item.findViewById<TextView>(R.id.tvEstadoDeudor).text =
                "CUOTA PENDIENTE"

            item.findViewById<TextView>(R.id.tvActividadesDeudor).text =
                "Actividades: ${socio.actividades.joinToString(", ")}"

            contenedorDeudores.addView(item)
        }
    }
}