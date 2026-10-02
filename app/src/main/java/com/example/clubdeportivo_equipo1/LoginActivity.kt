package com.example.clubdeportivo_equipo1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import android.util.Patterns
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias a los elementos del XML
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPass)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val txtOlvidePassword = findViewById<TextView>(R.id.txtOlvidePassword)

        txtPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP && event.x <= txtPassword.compoundPaddingStart) {
                val visible = txtPassword.transformationMethod == null
                txtPassword.transformationMethod =
                    if (visible) PasswordTransformationMethod.getInstance() else null
                txtPassword.setSelection(txtPassword.text.length)
                v.performClick()
            }
            false
        }

        // Botón Iniciar Sesión
        btnIniciarSesion.setOnClickListener {

            val email = txtEmail.text.toString().trim()
            val password = txtPassword.text.toString()

            when {

                // VALIDACIÓN DEL EMAIL
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    txtEmail.error =
                        "Ingresá un correo válido (ej: nombre@gmail.com)"
                    txtEmail.requestFocus()
                }

                // VALIDACIÓN DE CONTRASEÑA
                password.length < 6 -> {
                    txtPassword.error =
                        "La contraseña debe tener al menos 6 caracteres"
                    txtPassword.requestFocus()
                }

                !password.any { it.isUpperCase() } -> {
                    txtPassword.error =
                        "La contraseña debe tener al menos una mayúscula"
                    txtPassword.requestFocus()
                }

                !password.any { it.isLowerCase() } -> {
                    txtPassword.error =
                        "La contraseña debe tener al menos una minúscula"
                    txtPassword.requestFocus()
                }

                !password.any { it.isDigit() } -> {
                    txtPassword.error =
                        "La contraseña debe tener al menos un número"
                    txtPassword.requestFocus()
                }

                // TODO ESTÁ CORRECTO
                else -> {
                    Toast.makeText(
                        this,
                        "Datos correctos. Iniciando sesión...",
                        Toast.LENGTH_LONG
                    ).show()

                    startActivity(
                        Intent(this, HomeActivity::class.java)
                    )
                }
            }
        }

        // Botón "Olvidé mi contraseña"
        txtOlvidePassword.setOnClickListener {

            val email = txtEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                txtEmail.error =
                    "Por favor, ingresá tu correo para recuperar tu contraseña"

                txtEmail.requestFocus()

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Se ha enviado un enlace de recuperación a: $email",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}