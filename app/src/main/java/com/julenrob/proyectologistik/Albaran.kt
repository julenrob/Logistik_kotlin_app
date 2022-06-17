package com.julenrob.proyectologistik

import java.sql.Time
import java.util.*

data class Albaran(var id:Int,
                   var fechaentrega: Date,
                   var horamin: String,
                   var horamax: String,
                   var cliente_dni: Cliente,
                   var estado: Int)

