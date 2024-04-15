package com.example.rent_mobile_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.rent_mobile_app.models.user.User
import com.google.gson.Gson

class StorageActions(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("rentalAppData", Context.MODE_PRIVATE)
    private val prefEditor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getCurrentUser(): User {
        val currentUser: String? = sharedPreferences.getString("currentUser", null)
        return if (currentUser != null) {
            Gson().fromJson(currentUser, User::class.java)
        } else User()
    }

    fun setCurrentUser(user: User) {
        prefEditor.putString("currentUser", Gson().toJson(user)).apply()
    }
//
//    fun getUsers(): MutableList<User> {
//        val usersString: String? = sharedPreferences.getString("users", null)
//        return if (usersString != null) {
//            val typeToken = object : TypeToken<MutableList<User>>() {}.type
//            Gson().fromJson(usersString, typeToken)
//        } else mutableListOf()
//    }
//
//    fun getProperties(): MutableList<Property> {
//        val propertiesString: String? = sharedPreferences.getString("properties", null)
//        return if (propertiesString != null) {
//            val typeToken = object : TypeToken<MutableList<Property>>() {}.type
//            Gson().fromJson(propertiesString, typeToken)
//        } else mutableListOf()
//    }
//
//    fun getCurrentUserProperties(): MutableList<Property> {
//        return getProperties()
//            .filter { it.owner == getCurrentUser().email } as MutableList<Property>
//    }
//
//    /**
//     * Load properties not owned by the current user
//     *
//     * @param currentUser current logged user
//     * @return mutable list loaded with properties not owned by the current user
//     */
//    private fun getNonUserProperties(currentUser: User): MutableList<Property> {
//        val properties = getProperties()
//        val result: MutableList<Property> = mutableListOf()
//
//        for (i in properties) {
//            if (i.owner != currentUser.email) {
//                result.add(i)
//            }
//        }
//
//        return result
//    }
//
//    fun saveUsers(users: MutableList<User>) {
//        prefEditor.putString("users", Gson().toJson(users)).apply()
//    }
//
//    fun saveNewUser(newUser: User) {
//        var users: MutableList<User> = getUsers()
//        users.add(newUser)
//        saveUsers(users)
//    }
//
//    fun saveProperties(properties: MutableList<Property>) {
//        prefEditor.putString("properties", Gson().toJson(properties)).apply()
//    }
//
//    fun saveNewProperty(newProperty: Property) {
//        val properties: MutableList<Property> = getProperties()
//        properties.add(newProperty)
//        saveProperties(properties)
//    }
//
//    fun updateProperty(newProperty: Property, rowPosition: Int) {
//        val properties = getCurrentUserProperties()
//        properties[rowPosition] = newProperty
//
//        //add current user properties to the other properties
//        properties.addAll(getNonUserProperties(getCurrentUser()))
//
//        saveProperties(properties)
//    }
//
//    fun deleteProperty(rowPosition: Int) {
//        val properties = getCurrentUserProperties()
//        properties.removeAt(rowPosition)
//
//        //add remaining user properties to the other properties
//        properties.addAll(getNonUserProperties(getCurrentUser()))
//
//        saveProperties(properties)
//    }
//
//
//
//    fun resetAll() {
//        prefEditor.clear().apply()
//    }
//
//    fun setFilteredProperties(filteredProperties: MutableList<Property>) {
//        prefEditor.putString("filteredProperties", Gson().toJson(filteredProperties)).apply()
//    }
//
//    fun getFilteredProperties(): MutableList<Property> {
//        val propertiesString: String? = sharedPreferences.getString("filteredProperties", null)
//        return if (propertiesString != null) {
//            val typeToken = object : TypeToken<MutableList<Property>>() {}.type
//            Gson().fromJson(propertiesString, typeToken)
//        } else mutableListOf()
//    }
}