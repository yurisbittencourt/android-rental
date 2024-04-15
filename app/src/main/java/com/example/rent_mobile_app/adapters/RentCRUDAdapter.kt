package com.example.rent_mobile_app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rent_mobile_app.R
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.models.property.PropertyTypeEnum

class RentCRUDAdapter(
    private var properties: MutableList<Property>,
    private val viewButtonHandler: (Int) -> Unit,
    private val updateButtonHandler: (Int) -> Unit,
    private val deleteButtonHandler: (Int) -> Unit
) : RecyclerView.Adapter<RentCRUDAdapter.RentCRUDViewHolder>() {

    inner class RentCRUDViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.button_view).setOnClickListener() {
                viewButtonHandler(adapterPosition)
            }

            itemView.findViewById<Button>(R.id.button_update).setOnClickListener() {
                updateButtonHandler(adapterPosition)
            }

            itemView.findViewById<Button>(R.id.button_delete).setOnClickListener() {
                deleteButtonHandler(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentCRUDViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rent_management_row_layout, parent, false)
        return RentCRUDViewHolder(view)
    }

    override fun getItemCount(): Int {
        return properties.size
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onBindViewHolder(holder: RentCRUDViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tvRentManagementRowTitle).text =
            properties[position].title
        holder.itemView.findViewById<TextView>(R.id.tvRentManagementRowPrice).text =
            "$${properties[position].rent}"
        holder.itemView.findViewById<TextView>(R.id.tvRentManagementRowAddress).text =
            properties[position].address

        when (properties[position].type) {
            PropertyTypeEnum.APARTMENT -> {
                holder.itemView.findViewById<ImageView>(R.id.ivRentManagementRowImage)
                    .setImageResource(R.drawable.apartment)
            }

            PropertyTypeEnum.BASEMENT -> {
                holder.itemView.findViewById<ImageView>(R.id.ivRentManagementRowImage)
                    .setImageResource(R.drawable.basement)
            }

            PropertyTypeEnum.CONDO -> {
                holder.itemView.findViewById<ImageView>(R.id.ivRentManagementRowImage)
                    .setImageResource(R.drawable.condo)
            }

            PropertyTypeEnum.HOUSE -> {
                holder.itemView.findViewById<ImageView>(R.id.ivRentManagementRowImage)
                    .setImageResource(R.drawable.house)
            }

            PropertyTypeEnum.TOWNHOUSE -> {
                holder.itemView.findViewById<ImageView>(R.id.ivRentManagementRowImage)
                    .setImageResource(R.drawable.town)
            }

            else -> holder.itemView.findViewById<ImageView>(R.id.ivRentManagementRowImage)
                .setImageResource(R.drawable.apartment)
        }
    }
}