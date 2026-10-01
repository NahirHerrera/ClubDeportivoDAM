package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPass)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val txtOlvidePassword = findViewById<TextView>(R.id.txtOlvidePassword)

        btnIniciarSesion.setOnClickListener {
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

            Toast.makeText(this, "Datos correctos. Iniciando sesion...", Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, HomeActivity::class.java))

                txtOlvidePassword.setOnClickListener {
                    val email = txtEmail.text.toString().trim()

                    // 1. Validamos que el usuario haya escrito su correo primero
                    if (email.isEmpty()) {
                        txtEmail.error = "Por favor, ingresa tu correo para recuperar tu contraseña"
                        txtEmail.requestFocus()
                        return@setOnClickListener
                    }
                    Toast.makeText(this, "Se ha enviado un enlace de recuperación a: $email", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }