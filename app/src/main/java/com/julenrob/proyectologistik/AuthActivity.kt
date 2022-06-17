package com.julenrob.proyectologistik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.julenrob.proyectologistik.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup(){
        title = "Autenticacion"

        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text?.isNotEmpty() == true && binding.etPassword.text?.isNotEmpty() == true){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.etEmail.text.toString(),
                        binding.etPassword.text.toString()).addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(it.result?.user?.email ?: "",it.result?.user?.uid ?: "")
                            } else {
                                showErrorAuthentication()
                            }
                    }
            }
        }
    }

    private fun showErrorAuthentication(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Correo o contraseña incorrectos. Por favor, repite el proceso.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, uid:String){
        val homeIntent : Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("uid", uid)
        }
        startActivity(homeIntent)
    }
}