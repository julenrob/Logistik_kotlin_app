package com.julenrob.proyectologistik

import java.io.Serializable
import java.util.*

data class Ruta(var id: Int,
                var transportista_id: Int,
                var camion_id: Int,
                var albaran_id: Int,
                var conductor_id: Int,
                var fecharuta: String) : Serializable

