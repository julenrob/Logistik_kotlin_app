package com.julenrob.proyectologistik

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface APIService {
    @GET()
    fun getConductorByUid(@Url url:String):Call<Conductor>

    @GET("rutas")
    suspend fun getRutas(@Query("conductor") conductor:String, @Query("fecharuta") fecha:String):Response<List<RutaExtendida>>

    @GET("rutas_agrupadas")
    suspend fun getRutasAgrupadas(@Query("conductor") conductor:String):Response<List<Ruta>>
}