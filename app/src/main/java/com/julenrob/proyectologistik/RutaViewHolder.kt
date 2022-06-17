package com.julenrob.proyectologistik

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.julenrob.proyectologistik.databinding.ItemRutaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RutaViewHolder(view:View): RecyclerView.ViewHolder(view) {

    private val binding = ItemRutaBinding.bind(view)
    private lateinit var context : Context


    fun bind(rutas:Ruta){
        binding.fecha.text = "Fecha de la Ruta: " + rutas.fecharuta
        binding.idTransportista.text = "Transportista: " + rutas.transportista_id
        binding.idCamion.text = "Asignado al camión: " + rutas.camion_id
        binding.idConductor.text = "Asignado al conductor: " + rutas.conductor_id

        context = binding.btnAbrirMapa.context

        binding.btnAbrirMapa.setOnClickListener {

            println("Se ha pulsado el boton")
            val mapIntent: Intent = Intent(this.context, MapActivity::class.java).apply {
                putExtra("idConductor", rutas.conductor_id.toString())
                putExtra("fecha", rutas.fecharuta)
            }
            startActivity(this.context, mapIntent, null)
        }
    }


}