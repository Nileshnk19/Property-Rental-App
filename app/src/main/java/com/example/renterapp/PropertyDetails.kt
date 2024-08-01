package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.renterapp.databinding.ActivityPropertyDetailsBinding
import com.example.renterapp.model.Rental
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class PropertyDetails : AppCompatActivity() {
    lateinit var binding: ActivityPropertyDetailsBinding

    var db = Firebase.firestore
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var watchdetail: Rental

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        setTitle("Property Details Screen")

        firebaseAuth = Firebase.auth

        if (intent != null) {
            val docID = intent.getStringExtra("DocumentId")
            val isFromWatchlist = intent.getBooleanExtra("fromwatchlist", false)

            if (isFromWatchlist == true) {

                db.collection("userdata").document(firebaseAuth.currentUser?.uid.toString())
                    .collection("watchlist").document(docID.toString()).get()
                    .addOnSuccessListener { document: DocumentSnapshot ->

                        var propertiesDetails: Rental? = document.toObject(Rental::class.java)

                        if (propertiesDetails == null) {
                            Log.d("Testing", "NO DATA")
                        }

                        watchdetail = propertiesDetails!!

                        binding.tvRenterType.text = propertiesDetails?.rentalType
                        binding.tvRenterPrice.text = propertiesDetails?.monthlyPrice.toString()
                        binding.tvRoom.text = propertiesDetails?.numberOfRooms
                        binding.tvRenterAddress.text = propertiesDetails?.address

                        Glide.with(binding.imageView.context)
                            .load(propertiesDetails?.imageUrl)
                            .into(binding.imageView)


                    }.addOnFailureListener { error ->
                        Log.e("TESTING", error.toString())
                    }

                binding.watchbtn.text = "Remove from Watchlist"
                binding.watchbtn.setOnClickListener {
                    db.collection("userdata").document(firebaseAuth.currentUser?.uid.toString())
                        .collection("watchlist").document(docID.toString())
                        .delete().addOnSuccessListener {
                            val watchlistIntent =
                                Intent(this@PropertyDetails, MyWatchList::class.java)
                            startActivity(watchlistIntent)
                            finish()
                        }.addOnFailureListener { error ->
                            Log.e("TESTING", error.toString())
                        }
                }

            } else {

                db.collection("rentalList").document(docID.toString()).get()
                    .addOnSuccessListener { document: DocumentSnapshot ->

                        var propertiesDetails: Rental? = document.toObject(Rental::class.java)

                        if (propertiesDetails == null) {
                            Log.d("Testing", "NO DATA")
                        }

                        watchdetail = propertiesDetails!!

                        binding.tvRenterType.text = propertiesDetails?.rentalType
                        binding.tvRenterPrice.text = propertiesDetails?.monthlyPrice.toString()
                        binding.tvRoom.text = propertiesDetails?.numberOfRooms
                        binding.tvRenterAddress.text = propertiesDetails?.address

                        Glide.with(binding.imageView.context)
                            .load(propertiesDetails?.imageUrl)
                            .into(binding.imageView)


                    }.addOnFailureListener { error ->
                        Log.e("TESTING", error.toString())
                    }

                binding.watchbtn.setOnClickListener {

                    if (firebaseAuth.currentUser != null) {
                        db.collection("userdata").document(firebaseAuth.currentUser?.uid.toString())
                            .collection("watchlist").add(watchdetail).addOnSuccessListener {
                                var watchListIntent =
                                    Intent(this@PropertyDetails, MyWatchList::class.java)
                                startActivity(watchListIntent)
                            }
                            .addOnFailureListener { error ->
                                Log.e("TESTING", error.toString())
                            }
                    } else {
                        val snackbar = Snackbar.make(binding.root, "You need to login first...", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
            }
        }
    }

    //code for displaying menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_items, menu)
        return true
    }

    //onCLick Menu Item Handler
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Mapview -> {
                val createListing_Intent = Intent(this@PropertyDetails, MainActivity::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.ListView -> {
                val createListing_Intent = Intent(this@PropertyDetails, RentalListView::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.Watchlist -> {
                Log.d("CurrentUser", firebaseAuth.currentUser.toString())
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@PropertyDetails, MyWatchList::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@PropertyDetails, LoginScreen::class.java)
                    startActivity(Loginintent)
                }
                true
            }

            R.id.login -> {
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@PropertyDetails, MainActivity::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@PropertyDetails, LoginScreen::class.java)
                    startActivity(Loginintent)
                }
                true
            }

            R.id.logout -> {
                firebaseAuth.signOut()
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}