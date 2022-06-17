package com.julenrob.proyectologistik

data class Transportista(
                            var id: Int,
                            var nombre: String,
                            var direccion: String,
                            var provincia: String,
                            var poblacion: String,
                            var cp: String,
                            var lat: Double,
                            var lon: Double
                         )
