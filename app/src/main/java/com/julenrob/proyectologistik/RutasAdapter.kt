package com.julenrob.proyectologistik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RutasAdapter(private val rutas:List<Ruta>):RecyclerView.Adapter<RutaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return RutaViewHolder(layoutInflater.inflate(R.layout.item_ruta, parent, false))
    }

    override fun onBindViewHolder(holder: RutaViewHolder, position: Int) {
        val item = rutas[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = rutas.size

}