package com.example.clubdeportivo_equipo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.filter

class ActividadAdapter(
    private val actividades: List<Actividad>
) : RecyclerView.Adapter<ActividadAdapter.ViewHolder>() {

    // IDs de las actividades seleccionadas
    private val seleccionadas = mutableSetOf<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvIdActividad)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreActividad)
        val tvCosto: TextView = view.findViewById(R.id.tvCostoPase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividad, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = actividades[position]
        holder.tvId.text = "Actividad #${a.IdActividad}"
        holder.tvNombre.text = a.nombre
        holder.tvCosto.text = "$${a.costo}"


        holder.itemView.isSelected = a.IdActividad in seleccionadas

        holder.itemView.setOnClickListener {
            if (!seleccionadas.add(a.IdActividad)) {
                seleccionadas.remove(a.IdActividad)
            }
            it.isSelected = a.IdActividad in seleccionadas
        }
    }

    override fun getItemCount() = actividades.size

    // Para usar desde la Activity al tocar "Inscribir"
    fun getSeleccionadas(): List<Actividad> =
        actividades.filter { it.IdActividad in seleccionadas }
}