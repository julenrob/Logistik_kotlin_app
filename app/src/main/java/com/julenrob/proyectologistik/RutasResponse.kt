package com.julenrob.proyectologistik

import com.google.gson.annotations.SerializedName

data class RutasResponse (@SerializedName("ruta") var rutas: List<Ruta>)
