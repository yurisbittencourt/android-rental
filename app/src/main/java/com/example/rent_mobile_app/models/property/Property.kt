package com.example.rent_mobile_app.models.property

import com.google.firebase.firestore.GeoPoint

class Property(
    var id: String,
    var title: String,
    var type: PropertyTypeEnum,
    var owner: String,
    var bedroom: Int,
    var bathroom: Int,
    var kitchen: Int,
    var closet: Int,
    var laundry: Int,
    var livingRoom: Int,
    var balcony: Int,
    var squareFeet: Double,
    var rent: Double,
    var description: String,
    var address: String,
    var city: String,
    var postalCode: String,
    var isRent: Boolean,
    var additionalInformation: String,
    var coordinates: GeoPoint
) {
    override fun toString(): String {
        return "Property(" +
                "Id='$id', " +
                "Title='$title', " +
                "type=$type, " +
                "owner=$owner, " +
                "bedroom=$bedroom, " +
                "bathroom=$bathroom, " +
                "kitchen=$kitchen, " +
                "closet=$closet, " +
                "laundry=$laundry, " +
                "livingRoom=$livingRoom, " +
                "balcony=$balcony, " +
                "squareFeet=$squareFeet, " +
                "rent=$rent, " +
                "description='$description', " +
                "address='$address', " +
                "city='$city', " +
                "postalCode='$postalCode', " +
                "isRent=$isRent, " +
                "additionalInformation=$additionalInformation" +
                "coordinates:$coordinates" +
                ")"
    }
}