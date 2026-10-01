package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListadoClientesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_clientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listadoClientes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val clientes = SociosRepository.listarActivos()
        findViewById<TextView>(R.id.tvTotalClientes).text = "Total: ${clientes.size} clientes"

        val contenedor = findViewById<LinearLayout>(R.id.contenedorClientes)
        for (socio in clientes) {
            val item = layoutInflater.inflate(R.layout.item_cliente, contenedor, false)
            item.findViewById<TextView>(R.id.tvNombre).text = socio.nombreCompleto.uppercase()
            item.findViewById<TextView>(R.id.tvDatos).text =
                "N° Socio: ${socio.numeroFormateado}  •  DNI: ${socio.dniFormateado}"
            item.findViewById<TextView>(R.id.tvActividades).text =
                socio.actividades.joinToString("\n") { "• $it" }
            contenedor.addView(item)
        }

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }
}
