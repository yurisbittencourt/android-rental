package com.example.rent_mobile_app.screens.property

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rent_mobile_app.adapters.RentCRUDAdapter
import com.example.rent_mobile_app.databinding.ActivityRentManagementLayoutBinding
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.models.user.User
import com.example.rent_mobile_app.repositories.PropertyRepository
import com.example.rent_mobile_app.repositories.UserRepository
import com.example.rent_mobile_app.screens.property.RentActivity.Companion.setRentActivityBehavior
import com.example.rent_mobile_app.screens.property.RentActivity.Companion.setRentActivityProperty
import com.example.rent_mobile_app.screens.property.RentActivity.Companion.setRowPosition
import com.example.rent_mobile_app.utils.MessageUtils.Companion.snack
import com.example.rent_mobile_app.utils.StorageActions
import com.example.rent_mobile_app.utils.SuperMain

class RentCRUDActivity : SuperMain() {

    private val TAG = this@RentCRUDActivity.toString()

    private lateinit var binding: ActivityRentManagementLayoutBinding
    private lateinit var storageActions: StorageActions
    private lateinit var adapter: RentCRUDAdapter

    private lateinit var propertyRepository: PropertyRepository
    private lateinit var userRepository: UserRepository
    private var favoriteIdToDelete = ""

    private var users: MutableList<User> = mutableListOf()
    private var properties: MutableList<Property> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityRentManagementLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(binding.toolbar)

        storageActions = StorageActions(this)

        setRepository()
        setAdapter(properties)

        setBackButton(binding.buttonBack)

        getPropertiesByUser()
    }

    private fun setRepository() {
        propertyRepository = PropertyRepository(this)
        userRepository = UserRepository(this)
    }

    private fun getPropertiesByUser() {
        propertyRepository.getPropertiesByUser(currentUser.email)
    }

    private fun viewButtonHandler(rowPosition: Int) {
        redirectRent()
        setRentActivityProperty = properties[rowPosition]
        setRentActivityBehavior = "VIEW"
    }

    private fun setAdapter(properties: MutableList<Property>) {
        adapter = RentCRUDAdapter(
            properties,
            { pos: Int -> viewButtonHandler(pos) },
            { pos: Int -> updateButtonHandler(pos) },
            { pos: Int -> deleteButtonHandler(pos) }
        )
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    override fun onResume() {
        super.onResume()

        //properties
        propertyRepository.properties.observe(this, androidx.lifecycle.Observer { data ->
            if (data.isNotEmpty()) {
                properties.clear()
                properties.addAll(data)
                setAdapter(properties)
            } else {
                binding.tvGeneralMessage.text = "No favorites added"
                binding.tvGeneralMessage.visibility = View.VISIBLE
            }
        })

        //users
        userRepository.users.observe(this, androidx.lifecycle.Observer { data ->
            if (data.isNotEmpty()) {
                users.clear()
                users.addAll(data)
                userRepository.deleteIfPropertyIsFavorite(users, favoriteIdToDelete)
            }
        })
    }

    private fun updateButtonHandler(rowPosition: Int) {
        redirectRent()
        setRentActivityProperty = properties[rowPosition]
        setRentActivityBehavior = "UPDATE"
        setRowPosition = rowPosition
    }

    private fun deleteButtonHandler(position: Int) {
        Log.e(TAG, ">>> deleteButtonHandler: process start")

        try {
            //removing from db property
            propertyRepository.delete(properties[position])

            //removing from favorites
            favoriteIdToDelete = properties[position].id
            userRepository.getAll()

            //removing from screen
            properties.remove(properties[position])

            setAdapter(properties)
            snack(binding.root, "Property deleted!")
        } catch (e: Exception) {
            snack(binding.root, "Not possible to save, please, try again!")
        }
    }
}