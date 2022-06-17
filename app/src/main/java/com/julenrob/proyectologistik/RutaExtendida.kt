package com.julenrob.proyectologistik

import java.io.Serializable
import java.util.*

data class RutaExtendida(var id: Int,
                var transportista: Transportista,
                var camion: Camion,
                var albaran: Albaran,
                var conductor: Conductor,
                var fecharuta: String) : Serializable