package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.renterapp.databinding.ActivityLoginScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginScreen : AppCompatActivity() {
    lateinit var binding: ActivityLoginScreenBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        setTitle("Login Screen")

        //-----------------------------------------------------------------

        firebaseAuth = Firebase.auth

        //Login button click handler
        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email != "") {
                if (password != "") {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val nextScreenIntent =
                                    Intent(this@LoginScreen, MainActivity::class.java)
                                startActivity(nextScreenIntent)
                            } else {
                                binding.tvErrorLogin.visibility = View.VISIBLE
                                binding.tvErrorLogin.text = "Incorrect Email or Password..."
                            }
                        }
                } else {
                    binding.tvErrorLogin.visibility = View.VISIBLE
                    binding.tvErrorLogin.text = "Please, Enter your Password..."
                }
            } else {
                binding.tvErrorLogin.visibility = View.VISIBLE
                binding.tvErrorLogin.text = "Please, Enter your Email..."
            }
        }
    }

}