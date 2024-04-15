package com.example.rent_mobile_app.screens.main

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rent_mobile_app.adapters.MainAdapter
import com.example.rent_mobile_app.databinding.ActivityMainBinding
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.models.property.PropertyTypeEnum
import com.example.rent_mobile_app.repositories.PropertyRepository
import com.example.rent_mobile_app.repositories.UserRepository
import com.example.rent_mobile_app.screens.property.RentActivity
import com.example.rent_mobile_app.utils.EnumUtils.Companion.getPropertyType
import com.example.rent_mobile_app.utils.MessageUtils.Companion.superMessage
import com.example.rent_mobile_app.utils.StorageActions
import com.example.rent_mobile_app.utils.SuperMain
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : SuperMain(), GoogleMap.OnMarkerClickListener {

    private val TAG = this@MainActivity.toString()

    private lateinit var binding: ActivityMainBinding

    private lateinit var userRepository: UserRepository
    private lateinit var propertyRepository: PropertyRepository
    private lateinit var storageActions: StorageActions

    private lateinit var adapter: MainAdapter
    private var properties: MutableList<Property> = mutableListOf()
    private var adapterPropertyList: MutableList<Property> = mutableListOf()

    private var mapToggle: Boolean = true
    private var markerTag: String? = null
    private var instanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        setToolbar(binding.toolbar)
        instanceState = savedInstanceState

        setAddress(binding.etAddress)

        storageActions = StorageActions(this)
        userRepository = UserRepository(this)
        propertyRepository = PropertyRepository(this)

        propertyRepository.getAll()
        storageActions.getCurrentUser().id.let { if (it != "id") userRepository.get(it) }

        setPropertyTypeSpinner()
        setSortSpinner()
        setSortSpinnerOnChangeListener()

        binding.btnSetFilter.setOnClickListener { setAdapterPropertyList(properties) }
        binding.btnToggleView.setOnClickListener { toggleMap() }


        superMessage(binding.root)
    }

    private fun onMapReady(googleMap: GoogleMap, propertyList: MutableList<Property>) {
        val location = LatLng(coordinate.latitude, coordinate.longitude)
        val radius = binding.etRadiusFilter.text.toString().toDoubleOrNull()

        if (location.latitude != 0.0 && location.longitude != 0.0 && radius != null) {
            val circleOptions = CircleOptions()
                .center(location)
                .radius(radius * 1000)
                .strokeWidth(2f)
                .strokeColor(Color.CYAN)
                .fillColor(Color.argb(40, 0, 200, 255))
            googleMap.addCircle(circleOptions)
        }


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13.0f))

        for (property in propertyList) {
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(property.coordinates.latitude, property.coordinates.longitude))
                    .title(property.title)
                    .snippet("$${property.rent}")
            )
            if (marker != null) {
                marker.tag = property.id
            }
        }

        googleMap.setOnMarkerClickListener(this)
    }

    private fun setAdapterPropertyList(propertyList: MutableList<Property>) {
        val radiusFilter = binding.etRadiusFilter.text.toString().toDoubleOrNull()
        val spinnerValue = binding.spinnerPropertyType.selectedItem.toString()
        val addressField = binding.etAddress.text.toString()

        val coordinates = forwardGeocoding(addressField)

        binding.mapView.onCreate(instanceState)

        if (coordinates == null) {
            return
        } else {
            coordinate.latitude = coordinates[0]
            coordinate.longitude = coordinates[1]
        }

        val filteredByRadius =
            if (currentUserLocationPermission &&
                radiusFilter != null &&
                radiusFilter != 0.0
            ) {
                filterPropertiesByProximity(
                    propertyList,
                    radiusFilter,
                    coordinate.latitude,
                    coordinate.longitude
                )
            } else {
                properties
            }
        adapterPropertyList = if (spinnerValue == "ALL") {
            filteredByRadius
        } else {
            filterPropertiesByType(filteredByRadius, getPropertyType(spinnerValue))
        }

        setAdapter(adapterPropertyList)
        binding.mapView.getMapAsync { googleMap -> onMapReady(googleMap, adapterPropertyList) }
        binding.tvListTitle.text = "Results: ${adapterPropertyList.size}"
    }

    override fun onResume() {
        super.onResume()

        propertyRepository.properties.observe(this, androidx.lifecycle.Observer { data ->
            if (data != null) {
                properties.clear()
                properties.addAll(data.filter { it.isRent })
                setAdapterPropertyList(properties)
            }
        })

        userRepository.users.observe(this, androidx.lifecycle.Observer { result ->
            if (result != null) {
                storageActions.setCurrentUser(result[0])
                currentUser = result[0]
            }
        })

        setCoordinates()
    }

    private fun setAdapter(properties: MutableList<Property>) {
        adapter = MainAdapter(
            properties,
            currentUser,
            { pos: Int -> viewButtonHandler(pos) },
            { pos: Int -> addFavoriteButtonHandler(pos) })

        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun toggleMap() {
        if (mapToggle) {
            binding.mapView.visibility = View.GONE
            binding.rvMain.visibility = View.VISIBLE
            binding.llListHeader.visibility = View.VISIBLE
            binding.btnToggleView.text = "Map"
        } else {
            binding.mapView.visibility = View.VISIBLE
            binding.rvMain.visibility = View.GONE
            binding.llListHeader.visibility = View.GONE
            binding.btnToggleView.text = "List"
        }

        mapToggle = !mapToggle
    }

    private fun viewButtonHandler(position: Int) {
        RentActivity.setRentActivityProperty = properties[position]
        RentActivity.setRentActivityBehavior = "VIEW"
        redirectRent()
    }

    private fun addFavoriteButtonHandler(position: Int) {
        Log.i(this.toString(), currentUser.type)
        if (
            (currentUser.type == "TENANT") ||
            (currentUser.type == "LANDLORD")
        ) {
            if (currentUser.favoritedProperties.contains(adapterPropertyList[position].id)) {
                currentUser.favoritedProperties.remove(adapterPropertyList[position].id)
                userRepository.update(currentUser)
                setAdapterPropertyList(adapterPropertyList)
            } else {
                currentUser.favoritedProperties.add(adapterPropertyList[position].id)
                userRepository.update(currentUser)
                setAdapterPropertyList(adapterPropertyList)
            }
        } else {
            redirectLogin()
        }
    }

    private fun filterPropertiesByType(
        properties: MutableList<Property>,
        typeFilter: PropertyTypeEnum
    ): MutableList<Property> {
        return properties.filter { it.type == typeFilter } as MutableList<Property>
    }

    private fun filterPropertiesByProximity(
        properties: MutableList<Property>,
        radius: Double,
        userLat: Double,
        userLon: Double
    ): MutableList<Property> {
        return properties.filter {
            calculateDistance(
                userLat,
                userLon,
                it.coordinates.latitude,
                it.coordinates.longitude
            ) <= radius
        } as MutableList<Property>
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        //Apply Haversine formula to calculate distance between 2 coordinates
        val r = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun setPropertyTypeSpinner() {
        val propertyTypes =
            mutableListOf<String>("ALL", "APARTMENT", "BASEMENT", "CONDO", "HOUSE", "TOWNHOUSE")

        val propertyTypesAdapter =
            ArrayAdapter(this, R.layout.simple_spinner_item, propertyTypes.map { it })

        binding.spinnerPropertyType.adapter = propertyTypesAdapter
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (markerTag == marker.tag) {
            redirectRent()
            RentActivity.setRentActivityProperty = adapterPropertyList.find { it.id == markerTag }
            RentActivity.setRentActivityBehavior = "VIEW"
        } else {
            markerTag = marker.tag.toString()
        }
        Log.i(this.toString(), ">>> Marker: $markerTag")
        return false
    }

    private fun setSortSpinner() {
        val sortTypes =
            mutableListOf<String>("Sort by price", "Price low to high", "Price high to low")

        val sortTypesAdapter =
            ArrayAdapter(this, R.layout.simple_spinner_item, sortTypes.map { it })

        binding.spinnerSort.adapter = sortTypesAdapter
    }

    private fun setSortSpinnerOnChangeListener() {
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                var sortedList: List<Property>
                when (position) {
                    0 -> {
                        return
                    }
                    1 -> {
                        sortedList = adapterPropertyList.sortedBy { it.rent }.toMutableList()
                        setAdapterPropertyList(sortedList)
                    }
                    2 -> {
                        sortedList = adapterPropertyList.sortedByDescending { it.rent }.toMutableList()
                        setAdapterPropertyList(sortedList)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}