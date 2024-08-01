package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.renterapp.databinding.ActivityMainBinding
import com.example.renterapp.model.MapCoordinates
import com.example.renterapp.model.Rental
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var mMap: GoogleMap
    var firestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        setTitle("Search Screen")

        //-----------------------------------------------------------------------------------

        firebaseAuth = Firebase.auth

        // Initialize map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            // Set up map settings
            mMap.uiSettings.isZoomControlsEnabled = true
            // Load property data from Firestore
            loadPropertyData()
        }
    }

    private fun loadPropertyData() {
        // Retrieve property data from Firestore
        firestore.collection("rentalList")
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val rentalList = mutableListOf<Rental>()
                for (document in querySnapshot.documents) {
                    val rental = document.toObject(Rental::class.java)
                    rental?.let {
                        addMarker(it)
                        rentalList.add(it)
                    }
                }
                // Move and animate the camera to focus on the markers
                if (rentalList.isNotEmpty()) {
                    moveCameraToMarkers(rentalList)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load property data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun moveCameraToMarkers(rentalList: List<Rental>) {
        val builder = LatLngBounds.Builder()
        rentalList.forEach { rental ->
            val coordinates = MapCoordinates(rental.latitude, rental.longitude)
            val latLng = LatLng(coordinates.latitude, coordinates.longitude)
            builder.include(latLng)
        }
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }


    private fun addMarker(rental: Rental) {
        val coordinates = MapCoordinates(rental.latitude, rental.longitude)
        val latLng = LatLng(coordinates.latitude, coordinates.longitude)
        val marker = mMap.addMarker(MarkerOptions().position(latLng).title(rental.rentalType))
        marker?.tag = rental // Attach rental object to marker

        // Set click listener for marker
        mMap.setOnMarkerClickListener { clickedMarker ->
            val clickedRental = clickedMarker.tag as? Rental
            clickedRental?.let {
                // Redirect to PropertyDetails with rental ID
                val intent = Intent(this@MainActivity, PropertyDetails::class.java)
                intent.putExtra("DocumentId", clickedRental.id)
                startActivity(intent)
            }
            true // Consume the event
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
                val createListing_Intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.ListView -> {
                val createListing_Intent = Intent(this@MainActivity, RentalListView::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.Watchlist -> {
                Log.d("CurrentUser", firebaseAuth.currentUser.toString())
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@MainActivity, MyWatchList::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@MainActivity, LoginScreen::class.java)
                    startActivity(Loginintent)
                }
                true
            }

            R.id.login -> {
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@MainActivity, LoginScreen::class.java)
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