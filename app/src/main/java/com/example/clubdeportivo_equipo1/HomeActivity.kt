package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnRegistrarCliente).setOnClickListener {
            startActivity(Intent(this, RegistrarClienteActivity::class.java))
        }

        findViewById<Button>(R.id.btnCobrarCuota).setOnClickListener {
            startActivity(Intent(this, CobrarCuotaActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoDeClientes).setOnClickListener {
            startActivity(Intent(this, ListadoClientesActivity::class.java))
        }

        findViewById<Button>(R.id.btnListadoDeDeudores).setOnClickListener {
            startActivity(Intent(this, ListadoDeudoresActivity::class.java))
        }

        findViewById<Button>(R.id.btnActividad).setOnClickListener {
            startActivity(Intent(this, ActividadActivity::class.java))
        }

        findViewById<TextView>(R.id.tvCerrarSesion).setOnClickListener {
            Toast.makeText(this, "Cerrando Sesión...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))

        }

    }
}