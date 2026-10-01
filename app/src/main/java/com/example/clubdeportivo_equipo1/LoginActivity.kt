package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPass)
        val btnLogin = findViewById<Button>(R.id.btnIniciarSesion)

        btnLogin.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString().trim()

            if (email.isEmpty()) {
                txtEmail.error = "El campo correo electrónico es obligatorio"
                txtEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                txtPassword.error = "El campo contraseña es obligatorio"
                txtPassword.requestFocus()
                return@setOnClickListener
            }

            val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
            btnIniciarSesion.setOnClickListener {
                Toast.makeText(this, "Campos correctos. Iniciando sesión...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}