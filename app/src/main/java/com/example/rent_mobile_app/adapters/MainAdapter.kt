package com.example.rent_mobile_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rent_mobile_app.R
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.models.property.PropertyTypeEnum
import com.example.rent_mobile_app.models.user.User

class MainAdapter(

    private var propertyList: MutableList<Property>,
    private var currentUser: User,
    private val viewButtonHandler: (Int) -> Unit, private val deleteButtonHandler: (Int) -> Unit,
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                viewButtonHandler(adapterPosition)
            }
            itemView.findViewById<ImageButton>(R.id.btnMainFavorite).setOnClickListener {
                deleteButtonHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_row_layout, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tvMainTitle).text =
            propertyList[position].title
        holder.itemView.findViewById<TextView>(R.id.tvMainAddress).text =
            propertyList[position].address
        holder.itemView.findViewById<TextView>(R.id.tvMainPrice).text =
            "$${propertyList[position].rent}"
        holder.itemView.findViewById<TextView>(R.id.tvMainBed).text =
            "${propertyList[position].bedroom} Beds"
        holder.itemView.findViewById<TextView>(R.id.tvMainBath).text =
            "${propertyList[position].bathroom} Baths"
        holder.itemView.findViewById<TextView>(R.id.tvMainSqft).text =
            "${propertyList[position].squareFeet} sq.ft."

        when (propertyList[position].type) {
            PropertyTypeEnum.APARTMENT -> {
                holder.itemView.findViewById<ImageView>(R.id.ivMain)
                    .setImageResource(R.drawable.apartment)
            }

            PropertyTypeEnum.BASEMENT -> {
                holder.itemView.findViewById<ImageView>(R.id.ivMain)
                    .setImageResource(R.drawable.basement)
            }

            PropertyTypeEnum.CONDO -> {
                holder.itemView.findViewById<ImageView>(R.id.ivMain)
                    .setImageResource(R.drawable.condo)
            }

            PropertyTypeEnum.HOUSE -> {
                holder.itemView.findViewById<ImageView>(R.id.ivMain)
                    .setImageResource(R.drawable.house)
            }

            PropertyTypeEnum.TOWNHOUSE -> {
                holder.itemView.findViewById<ImageView>(R.id.ivMain)
                    .setImageResource(R.drawable.town)
            }

            else -> holder.itemView.findViewById<ImageView>(R.id.ivMain)
                .setImageResource(R.drawable.apartment)
        }

        if(currentUser.favoritedProperties.contains(propertyList[position].id)){
            holder.itemView.findViewById<ImageButton>(R.id.btnMainFavorite).setImageResource(R.drawable.star_filled_icon)
        }

    }
}
