package com.example.renterapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.renterapp.adapter.RentalListAdapter
import com.example.renterapp.databinding.ActivityRentalListViewBinding
import com.example.renterapp.model.Rental
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class RentalListView : AppCompatActivity() {

    lateinit var propertylistadapter: RentalListAdapter

    val propertyData: MutableList<Rental> = mutableListOf()

    lateinit var firebaseAuth: FirebaseAuth
    var db = Firebase.firestore

    lateinit var binding: ActivityRentalListViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentalListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        setTitle("Show in a List Screen")

        //initialize firebaseAuth variable
        firebaseAuth = Firebase.auth

        propertylistadapter = RentalListAdapter(propertyData, DetailPropertyHandler)

        binding.rvPropertyList.adapter = propertylistadapter
        binding.rvPropertyList.layoutManager = LinearLayoutManager(this)
        binding.rvPropertyList.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        loadData()

    }

    private  fun loadData() {
        db.collection("rentalList").whereEqualTo("isAvailable",true).get().addOnSuccessListener {
            result: QuerySnapshot ->
            val data : MutableList<Rental> = mutableListOf()

            for (document: QueryDocumentSnapshot in result) {
                val rental: Rental = document.toObject(Rental::class.java)
                data.add(rental)
            }

            propertyData.clear()
            propertyData.addAll(data)
            propertylistadapter.notifyDataSetChanged()

        }.addOnFailureListener{
            error ->
            Log.e("TESTING", error.toString())
        }
    }

    val DetailPropertyHandler: (String) -> Unit = {
       docID: String ->
        val detailpropertyintent = Intent(this@RentalListView, PropertyDetails::class.java)
        detailpropertyintent.putExtra("DocumentId", docID)
        startActivity(detailpropertyintent)
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
                val createListing_Intent = Intent(this@RentalListView, MainActivity::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.ListView -> {
                val createListing_Intent = Intent(this@RentalListView, RentalListView::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.Watchlist -> {
                Log.d("CurrentUser", firebaseAuth.currentUser.toString())
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@RentalListView, MyWatchList::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@RentalListView, LoginScreen::class.java)
                    startActivity(Loginintent)
                }
                true
            }

            R.id.login -> {
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@RentalListView, MainActivity::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@RentalListView, LoginScreen::class.java)
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