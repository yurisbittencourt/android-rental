package com.example.rent_mobile_app.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rent_mobile_app.models.property.Property
import com.example.rent_mobile_app.utils.EnumUtils.Companion.getPropertyType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class PropertyRepository(private val context: Context) {

    private val TAG = this.toString()
    private val firedb = Firebase.firestore
    private val propertiesCollection = "properties"

    private val fieldId = "id"
    private val fieldTitle = "title"
    private val fieldType = "type"
    private val fieldOwner = "owner"
    private val fieldBedroom = "bedroom"
    private val fieldBathroom = "bathroom"
    private val fieldKitchen = "kitchen"
    private val fieldCloset = "closet"
    private val fieldLaundry = "laundry"
    private val fieldLivingRoom = "livingRoom"
    private val fieldBalcony = "balcony"
    private val fieldSquareFeet = "squareFeet"
    private val fieldRent = "rent"
    private val fieldDescription = "description"
    private val fieldAddress = "address"
    private val fieldCity = "city"
    private val fieldPostalCode = "postalCode"
    private val fieldIsRent = "isRent"
    private val fieldAdditionalInformation = "additionalInformation"
    private val fieldCoordinates = "coordinates"

    var properties: MutableLiveData<List<Property>> = MutableLiveData<List<Property>>()

    fun save(newProperty: Property) {
        //TODO Take appropriate actions if email is already taken
        try {
            val data: MutableMap<String, Any> = HashMap()

            data[fieldId] = newProperty.id
            data[fieldTitle] = newProperty.title
            data[fieldType] = newProperty.type
            data[fieldOwner] = newProperty.owner
            data[fieldBedroom] = newProperty.bedroom
            data[fieldBathroom] = newProperty.bathroom
            data[fieldKitchen] = newProperty.kitchen
            data[fieldCloset] = newProperty.closet
            data[fieldLaundry] = newProperty.laundry
            data[fieldLivingRoom] = newProperty.livingRoom
            data[fieldBalcony] = newProperty.balcony
            data[fieldSquareFeet] = newProperty.squareFeet
            data[fieldRent] = newProperty.rent
            data[fieldDescription] = newProperty.description
            data[fieldAddress] = newProperty.address
            data[fieldCity] = newProperty.city
            data[fieldPostalCode] = newProperty.postalCode
            data[fieldIsRent] = newProperty.isRent
            data[fieldAdditionalInformation] = newProperty.additionalInformation
            data[fieldCoordinates] = newProperty.coordinates

            firedb.collection(propertiesCollection)
                .document(newProperty.id)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, ">>> save: Success! $docRef")
                }.addOnFailureListener { e ->
                    Log.e(TAG, ">>> save: Failure! $e")
                }
        } catch (e: Exception) {
            Log.e(TAG, ">>> save: Exception! $e")
        }
    }

    fun update(property: Property) {
        try {
            val data: MutableMap<String, Any> = HashMap()

            data[fieldTitle] = property.title
            data[fieldType] = property.type
            data[fieldOwner] = property.owner
            data[fieldBedroom] = property.bedroom
            data[fieldBathroom] = property.bathroom
            data[fieldKitchen] = property.kitchen
            data[fieldCloset] = property.closet
            data[fieldLaundry] = property.laundry
            data[fieldLivingRoom] = property.livingRoom
            data[fieldBalcony] = property.balcony
            data[fieldSquareFeet] = property.squareFeet
            data[fieldRent] = property.rent
            data[fieldDescription] = property.description
            data[fieldAddress] = property.address
            data[fieldCity] = property.city
            data[fieldPostalCode] = property.postalCode
            data[fieldIsRent] = property.isRent
            data[fieldAdditionalInformation] = property.additionalInformation
            data[fieldCoordinates] = property.coordinates

            firedb.collection(propertiesCollection)
                .document(property.id)
                .update(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, ">>> update: Success! $docRef")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, ">>> update: Failure! $e")
                }
        } catch (e: Exception) {
            Log.e(TAG, ">>> PropertyRepository.update: Exception! $e")
        }
    }

    fun getAll() {
        val tempPropList: MutableList<Property> = mutableListOf()

        firedb.collection("properties")
            .get()
            .addOnSuccessListener { results ->
                if (results != null) {
                    for (property in results) {
                        tempPropList.add(
                            Property(
                                property.data.getValue("id").toString(),
                                property.data.getValue("title").toString(),
                                getPropertyType(
                                    property.data.getValue("type").toString()
                                ),
                                property.data.getValue("owner").toString(),
                                property.data.getValue("bedroom").toString().toInt(),
                                property.data.getValue("bathroom").toString().toInt(),
                                property.data.getValue("kitchen").toString().toInt(),
                                property.data.getValue("closet").toString().toInt(),
                                property.data.getValue("laundry").toString().toInt(),
                                property.data.getValue("livingRoom").toString().toInt(),
                                property.data.getValue("balcony").toString().toInt(),
                                property.data.getValue("squareFeet").toString().toDouble(),
                                property.data.getValue("rent").toString().toDouble(),
                                property.data.getValue("description").toString(),
                                property.data.getValue("address").toString(),
                                property.data.getValue("city").toString(),
                                property.data.getValue("postalCode").toString(),
                                property.data.getValue("isRent").toString().toBoolean(),
                                property.data.getValue("additionalInformation").toString(),
                                property.getGeoPoint("coordinates")!!,
                            )
                        )
                    }
                    properties.postValue(tempPropList)
                } else {
                    Log.e(TAG, ">>> getAll: Failed! Properties was not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, ">>> getAll: Failed! Firestore query failed. $e")
            }
    }

    fun getAllFavorite(propertyIdList: MutableList<String>) {
        val tempPropList: MutableList<Property> = mutableListOf()

        firedb.collection("properties")
            .whereIn("id", propertyIdList)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (property in documents) {
                        tempPropList.add(
                            Property(
                                property.data.getValue("id").toString(),
                                property.data.getValue("title").toString(),
                                getPropertyType(
                                    property.data.getValue("type").toString()
                                ),
                                property.data.getValue("owner").toString(),
                                property.data.getValue("bedroom").toString().toInt(),
                                property.data.getValue("bathroom").toString().toInt(),
                                property.data.getValue("kitchen").toString().toInt(),
                                property.data.getValue("closet").toString().toInt(),
                                property.data.getValue("laundry").toString().toInt(),
                                property.data.getValue("livingRoom").toString().toInt(),
                                property.data.getValue("balcony").toString().toInt(),
                                property.data.getValue("squareFeet").toString().toDouble(),
                                property.data.getValue("rent").toString().toDouble(),
                                property.data.getValue("description").toString(),
                                property.data.getValue("address").toString(),
                                property.data.getValue("city").toString(),
                                property.data.getValue("postalCode").toString(),
                                property.data.getValue("isRent").toString().toBoolean(),
                                property.data.getValue("additionalInformation").toString(),
                                property.getGeoPoint("coordinates")!!,
                            )
                        )
                    }
                    properties.postValue(tempPropList)
                } else {
                    Log.e(TAG, ">>> getAllFavorite: Failed! Favorites was not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, ">>> getAllFavorite: Failed! firestore query failed. $e")
            }
    }

    fun getPropertiesByUser(email: String) {
        val tempPropList: MutableList<Property> = mutableListOf()
        firedb.collection("properties")
            .whereEqualTo("owner", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (property in documents) {
                        tempPropList.add(
                            Property(
                                property.data.getValue("id").toString(),
                                property.data.getValue("title").toString(),
                                getPropertyType(
                                    property.data.getValue("type").toString()
                                ),
                                property.data.getValue("owner").toString(),
                                property.data.getValue("bedroom").toString().toInt(),
                                property.data.getValue("bathroom").toString().toInt(),
                                property.data.getValue("kitchen").toString().toInt(),
                                property.data.getValue("closet").toString().toInt(),
                                property.data.getValue("laundry").toString().toInt(),
                                property.data.getValue("livingRoom").toString().toInt(),
                                property.data.getValue("balcony").toString().toInt(),
                                property.data.getValue("squareFeet").toString().toDouble(),
                                property.data.getValue("rent").toString().toDouble(),
                                property.data.getValue("description").toString(),
                                property.data.getValue("address").toString(),
                                property.data.getValue("city").toString(),
                                property.data.getValue("postalCode").toString(),
                                property.data.getValue("isRent").toString().toBoolean(),
                                property.data.getValue("additionalInformation").toString(),
                                property.getGeoPoint("coordinates")!!,
                            )
                        )
                    }
                    properties.postValue(tempPropList)
                } else {
                    Log.e(
                        TAG,
                        ">>> getPropertiesByUser: Failed! Properties was not found"
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    ">>> getPropertiesByUser: Failed! Firestore query failed. $e"
                )
            }
    }

    fun delete(property: Property) {
        try {
            firedb.collection(propertiesCollection).document(property.id).delete()
        } catch (e: Exception) {
            Log.e(TAG, ">>> delete: Error to delete property = $property!, exception = $e")
        }
    }
}