package com.julenrob.proyectologistik

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.julenrob.proyectologistik.databinding.ActivityHomeBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// PONER IP PRIVADA DE LA RED DE CLASE
private val PRIVATE_IP = "192.168.1.84"

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: RutasAdapter
    private val rutasMutableList = mutableListOf<Ruta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        println("Esto es el home")

        binding.swDarkMode.setOnCheckedChangeListener { switch, isChecked ->
            if (isChecked){

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        val bundle: Bundle? = intent.extras
        val email : String? = bundle?.getString("email")
        val uid : String? = bundle?.getString("uid")
        setup(email ?: "", uid ?: "")

        if (uid != null) {
            runBlocking {
                loadProfileByUid(uid)
                initRecyclerView()
            }
        }


    }

    private fun setup(email: String, uid: String){
        title = "Inicio"
        // binding.tvNombre.txt =
        binding.tvEmail.text = email

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("principal", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setNotification(){
        var builder = NotificationCompat.Builder(this, "principal")
            .setSmallIcon(R.drawable.logo_logistik_fixed)
            .setContentTitle("titulo")
            .setContentText("Se ha asignado una ruta nueva.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }
/*
    // Create an explicit intent for an Activity in your app
    val intentNotification = Intent(this, AlertDetails::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

    val builder = NotificationCompat.Builder(this, "principal")
        .setSmallIcon(R.drawable.logo_logistik_fixed)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // Set the intent that will fire when the user taps the notification
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

 */

    private fun showAPIError(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error en la petición a la API.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun getRetrofit(string: String = ""):Retrofit{

        return Retrofit.Builder()
            .baseUrl("http://$PRIVATE_IP:8000/api"+"$string/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }




    fun loadProfileByUid(uid_firebase:String) {
        println("Esto es loadProfileByUid")
            val call = getRetrofit("/conductor").create(APIService::class.java).getConductorByUid("$uid_firebase")
            call.enqueue(object : Callback<Conductor> {

                override fun onFailure(call: Call<Conductor>, t: Throwable) {
                    showAPIError()
                }

                override fun onResponse(call: Call<Conductor>, response: Response<Conductor>) {
                    println("Obtenidos los datos del Conductor!!!")
                    var conductor = response.body()

                    loadRecyclerViewRutas(conductor?.id.toString())
                }

            })

            // val conductor : Conductor? = call.body()

        // }
    }

    private fun initRecyclerView(){
        adapter = RutasAdapter(rutasMutableList)
        binding.rvRutas.layoutManager = LinearLayoutManager(this)
        binding.rvRutas.adapter = adapter
    }

    private fun loadRecyclerViewRutas(id_conductor:String){
        println("Esto es loadRecyclerViewRutas")
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getRutasAgrupadas("$id_conductor")
            println(call)
            val rutas = call.body()
            println(rutas)

            rutas?.forEach {
                Log.d("ID: ", it.id.toString())
                Log.d("Transportista: ", it.transportista_id.toString())
                Log.d("Camion: ", it.camion_id.toString())
                Log.d("Albaran: ", it.albaran_id.toString())
                Log.d("Conductor: ", it.conductor_id.toString())
                Log.d("Fecharuta: ", it.fecharuta)
                println(it.transportista_id)
                println(it.camion_id)
                println(it.conductor_id)
                println(it)
            }

            runOnUiThread {
                if (call.isSuccessful){
                    val registros : List<Ruta> = rutas ?: emptyList()

                    rutasMutableList.clear()
                    rutasMutableList.addAll(registros)
                    adapter.notifyDataSetChanged()
                } else {
                    showAPIError()
                    println("-----> Error en Rutas.")
                }
            }
        }
    }
}