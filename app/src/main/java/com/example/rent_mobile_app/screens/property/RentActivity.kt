package com.example.rent_mobile_app.screens.property

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.rent_mobile_app.databinding.ActivityRentPropertyBinding
import com.example.rent_mobile_app.models.coordinate.Coordinate
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.models.property.PropertyTypeEnum
import com.example.rent_mobile_app.repositories.PropertyRepository
import com.example.rent_mobile_app.utils.EnumUtils.Companion.getPropertyType
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.disable
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.invisible
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.setError
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.setNumberPikerPlus
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.startNumberPiker
import com.example.rent_mobile_app.utils.FieldsUtils.Companion.visible
import com.example.rent_mobile_app.utils.MessageUtils.Companion.snack
import com.example.rent_mobile_app.utils.MessageUtils.Companion.superMessage
import com.example.rent_mobile_app.utils.StorageActions
import com.example.rent_mobile_app.utils.SuperMain
import com.google.firebase.firestore.GeoPoint
import java.util.UUID

class RentActivity : SuperMain() {

    private val TAG = this@RentActivity.toString()

    companion object {
        var setRentActivityProperty: Property? = null
        var setRentActivityBehavior: String = ""
        var setRowPosition: Int = 0
    }

    private lateinit var binding: ActivityRentPropertyBinding
    private lateinit var storageActions: StorageActions
    private lateinit var propertyRepository: PropertyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityRentPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(binding.toolbar)

        propertyRepository = PropertyRepository(this)
        storageActions = StorageActions(this)

        setFields()
        setBehavior()
    }

    private fun setFields() {
        setNumberPickers()
        setPropertyTypeSpinner()
        setBackButton(binding.buttonBack)
        setBackButton(binding.buttonCancel)
        setLatLonSearchButton()
        setAddressSearchButton()
        setCurrentLocationButton()
        setSaveButton()
    }

    private fun setNumberPickers() {
        startNumberPiker(getScreenNumberPickers(com.example.rent_mobile_app.R.id.rentMainLayout))
        setNumberPikerPlus(binding.editSquareFeet)
    }

    private fun cleanBehavior() {
        setRentActivityBehavior = ""
        setRentActivityProperty = null
    }

    @SuppressLint("SetTextI18n")
    private fun setBehavior() {
        if (setRentActivityBehavior === "VIEW") {
            binding.labelScreenTitle.text = "VIEW RENTAL"
            loadFields()
            disableFields()
            cleanBehavior()
        } else if (setRentActivityBehavior === "UPDATE") {
            binding.labelScreenTitle.text = "UPDATE RENTAL"
            loadFields()
        }
    }

    private fun loadFields() {
        loadNumberPickers()

        binding.editAdTitle.setText(setRentActivityProperty!!.title)
        binding.spinnerPropertyType.setSelection(setRentActivityProperty!!.type.ordinal)
        binding.editRentPrice.setText(setRentActivityProperty!!.rent.toString())
        binding.editDescription.setText(setRentActivityProperty!!.description)
        binding.editLagitude.setText(setRentActivityProperty!!.coordinates.latitude.toString())
        binding.editLongitude.setText(setRentActivityProperty!!.coordinates.longitude.toString())
        binding.editAddress.setText(setRentActivityProperty!!.address)
        binding.editCity.setText(setRentActivityProperty!!.city)
        binding.editZipCode.setText(setRentActivityProperty!!.postalCode)
        binding.editAdditionalInformation.setText(setRentActivityProperty!!.additionalInformation)
        binding.switchActive.isChecked = setRentActivityProperty!!.isRent
    }

    private fun loadNumberPickers() {
        if (setRentActivityBehavior == "VIEW") {
            getScreenNumberPickers(com.example.rent_mobile_app.R.id.rentMainLayout).map {
                invisible(
                    it
                )
            }
            visible(binding.layoutStructure1)
            visible(binding.layoutStructure2)

            //load for labels
            binding.labelBedRoom.text = setRentActivityProperty!!.bedroom.toString()
            binding.labelBathRoom.text = setRentActivityProperty!!.bathroom.toString()
            binding.labelKitchen.text = setRentActivityProperty!!.kitchen.toString()
            binding.labelCloset.text = setRentActivityProperty!!.closet.toString()
            binding.labelLaundry.text = setRentActivityProperty!!.laundry.toString()
            binding.labelLivingRoom.text = setRentActivityProperty!!.livingRoom.toString()
            binding.labelBalcony.text = setRentActivityProperty!!.balcony.toString()
            binding.labelSquareFeet.text = setRentActivityProperty!!.squareFeet.toInt().toString()
        } else {
            //load for numbers
            binding.editBedroom.value = setRentActivityProperty!!.bedroom
            binding.editBathroom.value = setRentActivityProperty!!.bathroom
            binding.editKitchen.value = setRentActivityProperty!!.kitchen
            binding.editCloset.value = setRentActivityProperty!!.closet
            binding.editLaundry.value = setRentActivityProperty!!.laundry
            binding.editLivingRoom.value = setRentActivityProperty!!.livingRoom
            binding.editBalcony.value = setRentActivityProperty!!.balcony
            binding.editSquareFeet.value = setRentActivityProperty!!.squareFeet.toInt()
        }
    }

    private fun getCoordinates(): Coordinate? {
        Log.d(TAG, ">>> getCoordinates: process start")

        val address =
            "${binding.editAddress.text}, ${binding.editCity.text}, ${binding.editZipCode.text}"

        Log.d(TAG, ">>> getCoordinates: coordinates for $address")

        val locationCoord = forwardGeocoding(address)
        return if (locationCoord == null) {
            snack(binding.root, "Address not found!")

            setError(binding.editAddress)
            setError(binding.editCity)
            setError(binding.editZipCode)

            null
        } else {
            Coordinate(locationCoord[0], locationCoord[1])
        }
    }

    private fun getPropertyId(): String {
        return if (setRentActivityProperty == null) {
            UUID.randomUUID().toString()
        } else {
            setRentActivityProperty!!.id
        }
    }

    private fun setProperty(): Property? {
        var property: Property? = null
        val coordinates = getCoordinates()

        Log.d(TAG, ">>> setProperty: process start")

        try {
            property = Property(
                getPropertyId(),
                binding.editAdTitle.text.toString(),
                getPropertyType(binding.spinnerPropertyType.selectedItem.toString()),
                storageActions.getCurrentUser().email,
                binding.editBedroom.value,
                binding.editBathroom.value,
                binding.editKitchen.value,
                binding.editCloset.value,
                binding.editLaundry.value,
                binding.editLivingRoom.value,
                binding.editBalcony.value,
                binding.editSquareFeet.value.toDouble(),
                binding.editRentPrice.text.toString().toDouble(),
                binding.editDescription.text.toString(),
                binding.editAddress.text.toString(),
                binding.editCity.text.toString(),
                binding.editZipCode.text.toString(),
                binding.switchActive.isChecked,
                binding.editAdditionalInformation.text.toString(),
                GeoPoint(coordinates!!.latitude, coordinates.longitude)
            )
        } catch (e: Exception) {
            val message = ">>> setProperty: Error to set property!"
            Log.e(TAG, "$message, exception = $e")
            snack(binding.root, message)
        }

        return property
    }

    private fun disableFields() {
        Log.d(TAG, ">>> disableFields: process start")

        getScreenFields(com.example.rent_mobile_app.R.id.rentMainLayout).map { disable(it, false) }
        getScreenNumberPickers(com.example.rent_mobile_app.R.id.rentMainLayout).map {
            disable(
                it,
                false
            )
        }

        disable(binding.spinnerPropertyType, false)
        disable(binding.switchActive, false)
        invisible(binding.buttonAddressSearch)
        invisible(binding.buttonLatlngSearch)
        invisible(binding.buttonCurrentLocation)
        visible(binding.buttonBack)
        invisible(binding.buttonCancel)
        invisible(binding.buttonSave)
    }

    private fun setPropertyTypeSpinner() {
        val propertyTypes = PropertyTypeEnum.values().toMutableList()

        val propertyTypesAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, propertyTypes.map { it.name })

        binding.spinnerPropertyType.adapter = propertyTypesAdapter
    }

    private fun setLatLonSearchButton() {
        binding.buttonLatlngSearch.setOnClickListener {
            Log.d(TAG, ">>> setLatLonSearchButton: process start")

            if (currentUserLocationPermission) {
                if (
                    (!binding.editLagitude.text.isNullOrEmpty()) &&
                    (!binding.editLongitude.text.isNullOrEmpty())
                ) {
                    setReverseAddress(
                        binding.editAddress,
                        binding.editZipCode,
                        binding.editCity,
                        binding.editLagitude,
                        binding.editLongitude
                    )
                } else {
                    setError(binding.editLagitude)
                    setError(binding.editLongitude)

                    val message = "Latitude and longitude are required!"
                    Log.e(TAG, ">>> setLatLonSearchButton: $message")
                    snack(binding.root, message)
                }
            } else {
                val message = "Location permission is required!"
                Log.e(TAG, ">>> setLatLonSearchButton: $message")
                snack(binding.root, message)
            }
        }
    }

    private fun setAddressSearchButton() {
        binding.buttonAddressSearch.setOnClickListener {
            Log.d(TAG, ">>> setAddressSearchButton: process start")

            if (currentUserLocationPermission) {
                if (!binding.editAddress.text.isNullOrEmpty()) {
                    val coordinates = getCoordinates()

                    if (coordinates != null) {
                        binding.editLagitude.setText(coordinates.latitude.toString())
                        binding.editLongitude.setText(coordinates.longitude.toString())
                    }

                    Log.d(
                        TAG,
                        ">>> setAddressSearchButton: ${coordinates!!.latitude}, ${coordinates.longitude}"
                    )
                } else {
                    setError(binding.editAddress)

                    val message = "Address is required!"
                    Log.e(TAG, ">>> setAddressSearchButton: $message")
                    snack(binding.root, message)
                }
            } else {
                val message = "Location permission is required!"
                Log.e(TAG, ">>> setAddressSearchButton: $message")
                snack(binding.root, message)
            }
        }
    }

    private fun setCurrentLocationButton() {
        binding.buttonCurrentLocation.setOnClickListener {
            Log.d(TAG, ">>> setCurrentLocationButton: process start")

            if (currentUserLocationPermission) {
                setAddress(
                    binding.editAddress,
                    binding.editZipCode,
                    binding.editCity,
                    binding.editLagitude,
                    binding.editLongitude
                )
            } else {
                val message = "Location permission is required!"
                Log.e(TAG, ">>> setCurrentLocationButton: $message")
                snack(binding.root, message)
            }
        }
    }

    private fun setSaveButton() {
        binding.buttonSave.setOnClickListener {
            Log.d(TAG, ">>> setSaveButton: process start")

            if (fieldsValidation(binding.root, com.example.rent_mobile_app.R.id.rentMainLayout)) {
                val property = setProperty()

                if (property != null) {
                    if (setRentActivityBehavior == "UPDATE") {
                        try {
                            propertyRepository.update(property)
                            cleanBehavior()
                            superMessage = "Property updated!"
                        } catch (e: Exception) {
                            Log.e(TAG, ">>> setSaveButton: error to update property: $property")
                            snack(binding.root, "Not possible to update! please, try again!")
                        }
                    } else {
                        try {
                            propertyRepository.save(property)
                            superMessage = "Property created!"
                        } catch (e: Exception) {
                            Log.e(TAG, ">>> setSaveButton: error to save property: $property")
                            snack(binding.root, "Not possible to save, please, try again!")
                        }
                    }
                }

                redirectHome()
            }
        }
    }
}