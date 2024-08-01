package com.example.renterapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.renterapp.R
import com.example.renterapp.model.Rental

class RentalListAdapter(
    var rentalListData: List<Rental>,
    val detailProperty: (String) -> Unit
) : RecyclerView.Adapter<RentalListAdapter.RentalViewHolder>() {

    inner class RentalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.property_list_view, parent, false)
        return RentalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rentalListData.size
    }

    override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
        val currRentalItem: Rental = rentalListData.get(position)

        val rentalImage = holder.itemView.findViewById<ImageView>(R.id.img_rental)
        Glide.with(holder.itemView.context)
            .load(currRentalItem.imageUrl)
            .into(rentalImage)

        val rentalType = holder.itemView.findViewById<TextView>(R.id.tv_rentalType)
        val rentalPrice = holder.itemView.findViewById<TextView>(R.id.tv_rentalPrice)
        val numberOfRooms = holder.itemView.findViewById<TextView>(R.id.tv_numberOfRooms)
        val address = holder.itemView.findViewById<TextView>(R.id.tv_address)

        rentalType.text = currRentalItem.rentalType
        rentalPrice.text = "$${currRentalItem.monthlyPrice}"
        numberOfRooms.text = currRentalItem.numberOfRooms
        address.text = currRentalItem.address

        //property detail click handler
        holder.itemView.findViewById<ConstraintLayout>(R.id.cv_retalListView).setOnClickListener {
            detailProperty(currRentalItem.id)
        }
    }
}