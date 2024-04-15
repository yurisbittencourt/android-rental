package com.example.rent_mobile_app.models.user

import java.io.Serializable

class User(
    var id: String,
    var name: String,
    var email: String,
    var password: String,
    var phone: String,
    var type: String,
    var favoritedProperties: MutableList<String>,
    var ownedProperties: MutableList<String>
) : Serializable {

    constructor() : this(
        id = "id",
        name = "Guest",
        email = "N/A",
        password = "N/A",
        phone = "N/A",
        type = UserTypeEnum.GUEST.name,
        favoritedProperties = mutableListOf(),
        ownedProperties = mutableListOf()
    )

    override fun toString(): String {
        return "User(id='$id', " +
                "name='$name', " +
                "email='$email', " +
                "password='$password', " +
                "phone='$phone', " +
                "type=$type, " +
                "favoritedProperties=$favoritedProperties," +
                "ownedProperties=$ownedProperties)"
    }
}