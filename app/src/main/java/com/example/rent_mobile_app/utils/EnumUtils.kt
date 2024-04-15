package com.example.rent_mobile_app.utils

import com.example.rent_mobile_app.models.property.PropertyTypeEnum
import com.example.rent_mobile_app.models.user.UserTypeEnum

class EnumUtils {

    companion object {
        fun getUserType(text: String): UserTypeEnum {
            return enumValueOf(text)
        }

        fun getPropertyType(text: String): PropertyTypeEnum {
            return enumValueOf(text)
        }
    }
}