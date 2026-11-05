package com.example.clubdeportivo_equipo1

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadoClientesActivity : AppCompatActivity() {

    private lateinit var dbHelper: ClubDBHelper
    private lateinit var etBuscarDni: EditText
    private lateinit var tvMensaje: TextView
    private val adapter = ListadoClientesAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_clientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ClubDBHelper(this)
        etBuscarDni = findViewById(R.id.etBuscarDni)
        tvMensaje = findViewById(R.id.tvMensaje)

        findViewById<RecyclerView>(R.id.rvClientes).apply {
            layoutManager = LinearLayoutManager(this@ListadoClientesActivity)
            adapter = this@ListadoClientesActivity.adapter
        }

        findViewById<Button>(R.id.btnBuscar).setOnClickListener { buscarCliente() }
        etBuscarDni.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarCliente()
                true
            } else false
        }
        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        buscarCliente()
    }

    private fun buscarCliente() {
        val lista = dbHelper.listarClientes(etBuscarDni.text.toString().trim())
        adapter.actualizar(lista)
        tvMensaje.text = if (lista.isEmpty()) "No se encontraron clientes" else "${lista.size} cliente(s)"
    }
}