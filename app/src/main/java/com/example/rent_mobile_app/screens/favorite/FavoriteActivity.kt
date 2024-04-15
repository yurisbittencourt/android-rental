package com.example.rent_mobile_app.screens.favorite

import android.R
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rent_mobile_app.adapters.FavoritesAdapter
import com.example.rent_mobile_app.databinding.ActivityFavoritesBinding
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.repositories.PropertyRepository
import com.example.rent_mobile_app.repositories.UserRepository
import com.example.rent_mobile_app.screens.property.RentActivity
import com.example.rent_mobile_app.utils.SuperMain

class FavoriteActivity : SuperMain() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: FavoritesAdapter
    private lateinit var propertyRepository: PropertyRepository
    private lateinit var userRepository: UserRepository
    private var favoritePropertyList: MutableList<Property> = mutableListOf()
    private var sortedPropertyList: MutableList<Property> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbar)

        propertyRepository = PropertyRepository(this)
        userRepository = UserRepository(this)

        setFavPropsList()
        setSpinner()
        setSpinnerOnChangeListener()
    }

    private fun setFavPropsList() {
        if (currentUser.favoritedProperties.isNotEmpty()) {
            propertyRepository.getAllFavorite(currentUser.favoritedProperties)
        } else {
            binding.tvFavoritesTitle.text = "No favorites added"
        }
    }

    override fun onResume() {
        super.onResume()
        propertyRepository.properties.observe(this, androidx.lifecycle.Observer { data ->
            if (data != null) {
                favoritePropertyList.clear()
                favoritePropertyList.addAll(data)
                setAdapter(favoritePropertyList)
            }
        })
    }

    private fun setAdapter(dataList: MutableList<Property>) {
        adapter = FavoritesAdapter(
            dataList,
            { pos: Int -> viewButtonHandler(pos) },
            { pos: Int -> deleteButtonHandler(pos) })

        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        adapter.notifyDataSetChanged()
    }

    private fun viewButtonHandler(position: Int) {
        RentActivity.setRentActivityProperty = favoritePropertyList[position]
        RentActivity.setRentActivityBehavior = "VIEW"
        redirectRent()
    }

    private fun deleteButtonHandler(position: Int) {
        currentUser.favoritedProperties.remove(favoritePropertyList[position].id)
        userRepository.update(currentUser)
        favoritePropertyList.remove(favoritePropertyList[position])
        setAdapter(favoritePropertyList)
    }

    private fun setSpinner() {
        val sortTypes =
            mutableListOf<String>("\nSort by price\n", "\nPrice low to high\n", "\nPrice high to low\n")

        val sortTypesAdapter =
            ArrayAdapter(this, R.layout.simple_spinner_item, sortTypes.map { it })

        binding.spinnerSort.adapter = sortTypesAdapter
    }

    private fun setSpinnerOnChangeListener() {
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    return
                }
                if (position == 1) {
                    sortedPropertyList = favoritePropertyList.sortedBy { it.rent }.toMutableList()
                    setAdapter(sortedPropertyList)
                }
                if (position == 2) {
                    sortedPropertyList = favoritePropertyList.sortedByDescending { it.rent }.toMutableList()
                    setAdapter(sortedPropertyList)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}