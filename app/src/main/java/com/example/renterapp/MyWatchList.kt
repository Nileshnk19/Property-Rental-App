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
import com.example.renterapp.databinding.ActivityMyWatchListBinding
import com.example.renterapp.model.Rental
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class MyWatchList : AppCompatActivity() {
    lateinit var binding: ActivityMyWatchListBinding

    lateinit var watchlistadapter: RentalListAdapter
    val watchlistData: MutableList<Rental> = mutableListOf()
    var db = Firebase.firestore
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyWatchListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        setTitle("My Watch List Screen")

        firebaseAuth = Firebase.auth

        watchlistadapter = RentalListAdapter(watchlistData, DetailPropertyHandler)

        binding.rvWatchlist.adapter = watchlistadapter
        binding.rvWatchlist.layoutManager = LinearLayoutManager(this)
        binding.rvWatchlist.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        loadData()
    }
    private  fun loadData() {
        db.collection("userdata").document(firebaseAuth.currentUser?.uid.toString())
            .collection("watchlist").get().addOnSuccessListener {
                result: QuerySnapshot ->
            val data : MutableList<Rental> = mutableListOf()

            for (document: QueryDocumentSnapshot in result) {
                val rental: Rental = document.toObject(Rental::class.java)
                data.add(rental)
            }

            watchlistData.clear()
            watchlistData.addAll(data)
            watchlistadapter.notifyDataSetChanged()

        }.addOnFailureListener{
                error ->
            Log.e("TESTING", error.toString())
        }
    }

    val DetailPropertyHandler: (String) -> Unit = {
            docID: String ->
        val detailpropertyintent = Intent(this@MyWatchList, PropertyDetails::class.java)
        detailpropertyintent.putExtra("DocumentId", docID)
        detailpropertyintent.putExtra("fromwatchlist",true)
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
                val createListing_Intent = Intent(this@MyWatchList, MainActivity::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.ListView -> {
                val createListing_Intent = Intent(this@MyWatchList, RentalListView::class.java)
                startActivity(createListing_Intent)
                true
            }

            R.id.Watchlist -> {
                Log.d("CurrentUser", firebaseAuth.currentUser.toString())
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@MyWatchList, MyWatchList::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@MyWatchList, LoginScreen::class.java)
                    startActivity(Loginintent)
                }
                true
            }

            R.id.login -> {
                if (firebaseAuth.currentUser != null) {
                    val createListing_Intent = Intent(this@MyWatchList, MainActivity::class.java)
                    startActivity(createListing_Intent)
                } else {
                    var Loginintent = Intent(this@MyWatchList, LoginScreen::class.java)
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
