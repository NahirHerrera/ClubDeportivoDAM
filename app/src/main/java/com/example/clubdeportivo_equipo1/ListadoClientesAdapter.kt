package com.example.clubdeportivo_equipo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class ListadoClientesAdapter(private var items: List<ClienteListado>) : RecyclerView.Adapter<ListadoClientesAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvId: TextView = v.findViewById(R.id.tvIdCliente)
        val tvTipo: TextView = v.findViewById(R.id.tvTipoCliente)
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvDni: TextView = v.findViewById(R.id.tvDni)
        val tvCuota: TextView = v.findViewById(R.id.tvCuota)
        val tvFecha: TextView = v.findViewById(R.id.tvFechaInscripcion)
        val tvActividades: TextView = v.findViewById(R.id.tvActividades)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.activity_item_cliente, parent, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        val c = items[i]
        h.tvId.text = "ID Cliente: ${c.id}"
        h.tvTipo.text = c.tipo
        h.tvNombre.text = "${c.apellido}, ${c.nombre}"
        h.tvDni.text = "DNI: ${c.dni}"
        h.tvCuota.text = if (c.tipo == "Socio") "Cuota: Al día" else "Cuota: No aplica"
        h.tvFecha.text = "Fecha de inscripción: ${c.fecha ?: "-"}"
        h.tvActividades.text = c.actividades?.lines()?.joinToString("\n") { "• $it" } ?: "Sin actividades inscriptas"
    }

    override fun getItemCount() = items.size

    fun actualizar(nuevos: List<ClienteListado>) {
        items = nuevos
        notifyDataSetChanged()
    }
}