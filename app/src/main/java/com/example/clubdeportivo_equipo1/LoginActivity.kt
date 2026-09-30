package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        btnIniciarSesion.setOnClickListener {
            Toast.makeText(this, "¡Bienvenido al sistema!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}