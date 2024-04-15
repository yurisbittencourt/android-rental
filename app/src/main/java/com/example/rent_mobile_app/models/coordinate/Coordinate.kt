package com.example.rent_mobile_app.models.coordinate

class Coordinate(
    var latitude: Double,
    var longitude: Double
) {

    constructor() : this(0.0, 0.0)

    override fun toString(): String {
        return "Coordinate(latitude='$latitude', longitude='$longitude')"
    }
}