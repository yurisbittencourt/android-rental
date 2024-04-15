package com.example.rent_mobile_app.models.user

enum class UserTypeEnum(enum: String) {
    GUEST ("Guest"),
    TENANT("Tenant"),
    LANDLORD("Landlord")
}