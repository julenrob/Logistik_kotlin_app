package com.julenrob.proyectologistik

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.AccessController.getContext


// import com.julenrob.proyectologistik.HomeActivity as home

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    // PONER IP PRIVADA DE LA RED DE CLASE
    private val PRIVATE_IP = "192.168.1.84"
    // private lateinit var context : Context
    private lateinit var adapter: RutasAdapter
    private val rutasMutableListMapa = mutableListOf<RutaExtendida>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        createFragment()


    }

    private fun createFragment() {
        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val bundle: Bundle? = intent.extras
        val idConductor =  bundle?.getString("idConductor")
        val fecha =  bundle?.getString("fecha")
        println("ha llegado al mapa el id: "+ idConductor)
        println("ha llegado al mapa la fecha: "+ fecha)
        if (idConductor != null && fecha != null) {
            dataToMaps(idConductor, fecha, map)
        }


        rutasMutableListMapa.forEach{
            println("EEEEEOOOOO-> ")
            println(it.id)
        }




        // createMarker(map)
    }

    private fun createMarker(map: GoogleMap, lat: Double, lon: Double, title: String, snippet: String, color: Float) {
        val sydney = LatLng(lat, lon)
        map.addMarker(
            MarkerOptions()
                .position(sydney)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color))
        )
        println("Nuevo marker creado. Con el title " + title)
    }


    private fun showAPIError(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error en la petición a la API.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun getRetrofit(string: String = ""): Retrofit {

        return Retrofit.Builder()
            .baseUrl("http://$PRIVATE_IP:8000/api"+"$string/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    private fun dataToMaps(id_conductor:String, fecha : String, map : GoogleMap){
        println("Esto es loadRecyclerViewRutas")
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getRutas("$id_conductor", "$fecha")
            println(call)
            val rutas = call.body()
            println(rutas)

            var lastCliente = ""; var severalAlb = 0;

            rutas?.forEach { ruta ->

                println("RUTA: " + ruta)

                var albaranes = listOf(ruta.albaran)
                var dni = ruta.albaran.cliente_dni.dni
                // var albaranesList = ruta.albaran.id


                println("albaranes de " + ruta.albaran.cliente_dni.nombre)

                albaranes.forEach { alb ->
                    println(alb)
                    severalAlb += alb.id

                    // AQUI HAY QUE CONSEGUIR QUE severalAlb SEA LA CONCATENACIÓN DE LOS ALBARANES PARA UN MISMO CLIENTE, Y MOSTRARLO EN EL SNIPPET


                if (ruta.albaran.cliente_dni.dni != lastCliente){
                    runOnUiThread {
                        createMarker(
                            map,
                            ruta.albaran.cliente_dni.lat,
                            ruta.albaran.cliente_dni.lon,
                            "Cliente: " + ruta.albaran.cliente_dni.nombre + " " + ruta.albaran.cliente_dni.apellido1 + " " + ruta.albaran.cliente_dni.apellido2 + ", Código albarán: " + severalAlb,
                            "Horario de entrega de " + ruta.albaran.horamin + " a " + ruta.albaran.horamax,
                            BitmapDescriptorFactory.HUE_AZURE
                        )

                    }
                }
                    lastCliente = ruta.albaran.cliente_dni.dni

                }

            }

            runOnUiThread {
                if (call.isSuccessful){


                    rutas?.first()?.transportista?.let {
                        createMarker(
                            map,
                            it.lat,
                            rutas?.first()?.transportista.lon,
                            "Almacén: " + rutas?.first()?.transportista.nombre,
                            it.direccion,
                            BitmapDescriptorFactory.HUE_RED
                        )
                        val zoomLevel = 12.0f
                        val latlon = LatLng(it.lat, it.lon)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon, zoomLevel))
                    }
                }

            }


            val registros : List<RutaExtendida> = rutas ?: emptyList()
            rutasMutableListMapa.clear()
            rutasMutableListMapa.addAll(registros)

        }
    }

}