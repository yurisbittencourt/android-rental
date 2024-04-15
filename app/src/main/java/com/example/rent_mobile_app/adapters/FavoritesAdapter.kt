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

class FavoritesAdapter(
    private var propertyList: MutableList<Property>,
    private val viewButtonHandler: (Int) -> Unit, private val deleteButtonHandler: (Int) -> Unit,
) : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                viewButtonHandler(adapterPosition)
            }
            itemView.findViewById<ImageButton>(R.id.btnFavoritesFavorite).setOnClickListener {
                deleteButtonHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorites_row_layout, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tvFavoritesTitle).text =
            propertyList[position].title
        holder.itemView.findViewById<TextView>(R.id.tvFavoritesAddress).text =
            propertyList[position].address
        holder.itemView.findViewById<TextView>(R.id.tvFavoritesPrice).text =
            "$${propertyList[position].rent}"
        holder.itemView.findViewById<TextView>(R.id.tvFavoritesBed).text =
            "${propertyList[position].bedroom} Beds"
        holder.itemView.findViewById<TextView>(R.id.tvFavoritesBath).text =
            "${propertyList[position].bathroom} Baths"
        holder.itemView.findViewById<TextView>(R.id.tvFavoritesSqft).text =
            "${propertyList[position].squareFeet} sq.ft."

        when (propertyList[position].type) {
            PropertyTypeEnum.APARTMENT -> {
                holder.itemView.findViewById<ImageView>(R.id.ivFavorites)
                    .setImageResource(R.drawable.apartment)
            }

            PropertyTypeEnum.BASEMENT -> {
                holder.itemView.findViewById<ImageView>(R.id.ivFavorites)
                    .setImageResource(R.drawable.basement)
            }

            PropertyTypeEnum.CONDO -> {
                holder.itemView.findViewById<ImageView>(R.id.ivFavorites)
                    .setImageResource(R.drawable.condo)
            }

            PropertyTypeEnum.HOUSE -> {
                holder.itemView.findViewById<ImageView>(R.id.ivFavorites)
                    .setImageResource(R.drawable.house)
            }

            PropertyTypeEnum.TOWNHOUSE -> {
                holder.itemView.findViewById<ImageView>(R.id.ivFavorites)
                    .setImageResource(R.drawable.town)
            }

            else -> holder.itemView.findViewById<ImageView>(R.id.ivFavorites)
                .setImageResource(R.drawable.apartment)
        }
    }
}
