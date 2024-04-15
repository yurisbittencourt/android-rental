package com.example.rent_mobile_app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.rent_mobile_app.R
import com.example.rent_mobile_app.models.coordinate.Coordinate
import com.example.rent_mobile_app.models.user.User
import com.example.rent_mobile_app.repositories.UserRepository
import com.example.rent_mobile_app.screens.favorite.FavoriteActivity
import com.example.rent_mobile_app.screens.login.LoginActivity
import com.example.rent_mobile_app.screens.login.SignupActivity
import com.example.rent_mobile_app.screens.main.MainActivity
import com.example.rent_mobile_app.screens.property.RentActivity
import com.example.rent_mobile_app.screens.property.RentCRUDActivity
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.setError
import com.example.rent_mobile_app.utils.MessageUtils.Companion.snack
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

open class SuperMain : AppCompatActivity() {

    private val TAG = this@SuperMain.toString()

    private lateinit var storageActions: StorageActions
    private lateinit var userRepository: UserRepository

    var currentUserLocationPermission: Boolean = true
    var coordinate: Coordinate = Coordinate()
    private var coordinates: MutableLiveData<List<Coordinate>> = MutableLiveData<List<Coordinate>>()
    var address: MutableLiveData<List<Address>> = MutableLiveData<List<Address>>()

    companion object {
        var currentUser: User = User()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageActions = StorageActions(this)
        userRepository = UserRepository(this)

        locationCall()
    }

    //  #####>-  GEOCODING FUNCTIONS -<#####
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val APP_PERMISSIONS_LIST = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun locationCall() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
    }

    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultsList ->
        Log.d(TAG, resultsList.toString())

        var allPermissionsGrantedTracker = true
        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && !item.value) {
                allPermissionsGrantedTracker = false
            }
        }

        if (allPermissionsGrantedTracker) {
            getDeviceLocation()
        } else {
            currentUserLocationPermission = false
        }
    }

    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location === null) {
                    Log.d(this.toString(), ">>> getDeviceLocation: Location is null")
                    return@addOnSuccessListener
                }
                val coordinate = Coordinate(location.latitude, location.longitude)
                address.postValue(
                    listOf(
                        reverseGeocoding(
                            coordinate.latitude,
                            coordinate.longitude
                        )
                    ) as List<Address>?
                )
                coordinates.postValue(listOf(coordinate))
            }
    }

    @SuppressLint("SetTextI18n")
    fun setAddress(address: EditText) {
        this.address.observe(this, androidx.lifecycle.Observer { data ->
            if (data != null) {
                address.setText("${data[0].locality}, ${data[0].adminArea}")
            }
        })
    }

    fun setCoordinates() {
        coordinates.observe(this, androidx.lifecycle.Observer { data ->
            if (data != null) {
                coordinate = Coordinate(data[0].latitude, data[0].longitude)
                Log.i(TAG, "TESTE >>> ${coordinate.latitude}, ${coordinate.longitude}")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun setAddress(
        address: EditText,
        postalCode: EditText,
        city: EditText,
        latitude: EditText,
        longitude: EditText
    ) {
        this.address.observe(this, androidx.lifecycle.Observer { data ->
            if (data != null) {
                address.setText(data[0].thoroughfare)
                postalCode.setText(data[0].postalCode)
                city.setText("${data[0].locality}, ${data[0].adminArea} - ${data[0].countryName}")
                latitude.setText(data[0].latitude.toString())
                longitude.setText(data[0].longitude.toString())
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun setReverseAddress(
        address: EditText,
        postalCode: EditText,
        city: EditText,
        latitude: EditText,
        longitude: EditText
    ) {
        val reverse = reverseGeocoding(
            latitude.text.toString().toDouble(),
            longitude.text.toString().toDouble()
        )

        if (reverse != null) {
            address.setText(reverse.thoroughfare)
            postalCode.setText(reverse.postalCode)
            city.setText("${reverse.locality}, ${reverse.adminArea} - ${reverse.countryName}")
            latitude.setText(reverse.latitude.toString())
            longitude.setText(reverse.longitude.toString())
        }
    }

    fun forwardGeocoding(location: String): Array<Double>? {
        Log.i(TAG, ">>> forwardGeocoding: attempting $location")

        var lat: Double? = null
        var lon: Double? = null
        val geocoder = Geocoder(applicationContext, Locale.getDefault())

        try {
            val results: MutableList<Address>? = geocoder.getFromLocationName(location, 1)

            if ((results == null) || (results.size == 0)) {
                Log.e(TAG, ">>> forwardGeocoding: results = null || 0")
            } else {
                lat = results[0].latitude
                lon = results[0].longitude
                Log.i(this.toString(), ">>> $lat, $lon")
            }
        } catch (e: Exception) {
            Log.e(TAG, ">>> forwardGeocoding caught exception: $e")
        }

        return if (lat == null || lon == null) null else arrayOf(lat, lon)
    }

    private fun reverseGeocoding(lat: Double, lon: Double): Address? {
        var foundLocation: Address? = null
        val geocoder = Geocoder(applicationContext, Locale.getDefault())

        try {
            val results: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)

            if (results == null || results.size == 0) {
                Log.d(TAG, ">>> reverseGeocoding: 0 results")
            } else {
                foundLocation = results[0]
            }
        } catch (e: Exception) {
            Log.e(TAG, ">>> reverseGeocoding: caught exception: $e")
        }

        return foundLocation
    }

    fun fieldsValidation(root: View, layout: Int): Boolean {
        for (i in getScreenFields(layout)) {
            if (!fieldIsNullOrEmpty(root, i)) {
                return false
            }
        }

        return true
    }

    private fun fieldIsNullOrEmpty(root: View, field: EditText): Boolean {
        if (field.text.toString().isEmpty()) {
            snack(root, field.hint.toString())
            setError(field)
            return false
        }

        return true
    }

    fun getScreenNumberPickers(layout: Int): MutableList<NumberPicker> {
        Log.d(TAG, ">>> getScreenNumberPickers: process start")

        val fields = mutableListOf<NumberPicker>()

        val scrollView = findViewById<ScrollView>(layout)

        for (i in 0 until scrollView.childCount) {
            val child: View = scrollView.getChildAt(i)

            if (child is LinearLayout) {
                fields.addAll(getLinearLayoutNumberPicker(child))
            }
        }

        return fields
    }

    private fun getLinearLayoutNumberPicker(linearLayout: LinearLayout): MutableList<NumberPicker> {
        Log.d(TAG, ">>> getLinearLayoutNumberPicker: process start")

        val fields = mutableListOf<NumberPicker>()

        for (i in 0 until linearLayout.childCount) {
            val child: View = linearLayout.getChildAt(i)

            if (child is NumberPicker) {
                Log.d(TAG, ">>> getLinearLayoutNumberPicker: get field = ${child.id}")
                fields.add(child)
            } else if (child is LinearLayout) {
                fields.addAll(getLinearLayoutNumberPicker(child))
            }
        }

        return fields
    }

    fun getScreenFields(layout: Int): MutableList<EditText> {
        Log.d(TAG, ">>> getScreenFields: process start")

        val fields = mutableListOf<EditText>()

        val scrollView = findViewById<ScrollView>(layout)

        for (i in 0 until scrollView.childCount) {
            val child: View = scrollView.getChildAt(i)

            if (child is LinearLayout) {
                fields.addAll(getLinearLayoutFields(child))
            }
        }

        return fields
    }

    private fun getLinearLayoutFields(linearLayout: LinearLayout): MutableList<EditText> {
        Log.d(TAG, ">>> getLinearLayoutFields: process start")

        val fields = mutableListOf<EditText>()

        for (i in 0 until linearLayout.childCount) {
            val child: View = linearLayout.getChildAt(i)

            if (child is EditText) {
                Log.d(TAG, ">>> getLinearLayoutFields: get field = ${child.id}")
                fields.add(child)
            } else if (child is LinearLayout) {
                fields.addAll(getLinearLayoutFields(child))
            }
        }

        return fields
    }

    open fun setBackButton(button: Button) {
        button.setOnClickListener {
            redirectHome()
        }
    }

    private fun getUserName(): String {
        val name: String = storageActions.getCurrentUser().name
        return if (name != "N/A") name
        else "Guest"
    }

    //  #####>-  MENU FUNCTIONS -<#####
    fun setToolbar(toolbar: Toolbar) {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        setSupportActionBar(toolbar)

        supportActionBar?.title = getUserName()
    }

    private fun setMenuVisibility(menu: Menu, storage: StorageActions) {
        if (
            (storage.getCurrentUser().type == "LANDLORD") ||
            (storage.getCurrentUser().type == "TENANT")
        ) {
            if (storage.getCurrentUser().type == "LANDLORD") {
                menu.findItem(R.id.menu_action_postRental).isVisible = true
                menu.findItem(R.id.menu_action_rentalManagement).isVisible = true
            }

            menu.findItem(R.id.menu_action_logout).isVisible = true
            menu.findItem(R.id.menu_action_account).isVisible = true
        } else {
            menu.findItem(R.id.menu_action_login).isVisible = true
            menu.findItem(R.id.menu_action_signup).isVisible = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)

        if (menu != null) {
            setMenuVisibility(menu, storageActions)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_home -> {
                redirectHome()
                true
            }

            R.id.menu_action_login -> {
                redirectLogin()
                true
            }

            R.id.menu_action_signup -> {
                redirectSignUp()
                true
            }

            R.id.menu_action_logout -> {
                storageActions.setCurrentUser(User())
                currentUser = User()
                redirectHome()
                true
            }

            R.id.menu_action_account -> {
                redirectFavourite()
                true
            }

            R.id.menu_action_rentalManagement -> {
                redirectRentCRUD()
                true
            }

            R.id.menu_action_postRental -> {
                redirectRent()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //  #####>-  ROUTES FUNCTIONS -<#####
    fun redirectHome() {
        startActivity(Intent(this@SuperMain, MainActivity::class.java))
        finish()
    }

    fun redirectLogin() {
        startActivity(Intent(this@SuperMain, LoginActivity::class.java))
        finish()
    }

    fun redirectSignUp() {
        startActivity(Intent(this@SuperMain, SignupActivity::class.java))
        finish()
    }

    fun redirectFavourite() {
        startActivity(Intent(this@SuperMain, FavoriteActivity::class.java))
        finish()
    }

    fun redirectRent() {
        startActivity(Intent(this@SuperMain, RentActivity::class.java))
        finish()
    }

    fun redirectRentCRUD() {
        startActivity(Intent(this@SuperMain, RentCRUDActivity::class.java))
        finish()
    }
}