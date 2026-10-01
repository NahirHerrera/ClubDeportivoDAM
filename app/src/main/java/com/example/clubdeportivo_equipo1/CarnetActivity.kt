package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CarnetActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DNI = "extra_dni"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        // Si llega el DNI de un socio, se cargan sus datos en el carnet
        val dni = intent.getStringExtra(EXTRA_DNI)
        val socio = dni?.let { SociosRepository.buscarPorDni(it) }
        if (socio != null) {
            findViewById<TextView>(R.id.Nombre).text = socio.nombreCompleto.uppercase()
            findViewById<TextView>(R.id.Socio).text = "N° Socio: ${socio.numeroFormateado}"
            findViewById<TextView>(R.id.Dni).text = "DNI: ${socio.dniFormateado}"
            findViewById<TextView>(R.id.Vigencia).text =
                if (socio.activo && socio.cuotaAlDia) "Vigencia: ACTIVO" else "Vigencia: INACTIVO"
        }

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }
}
