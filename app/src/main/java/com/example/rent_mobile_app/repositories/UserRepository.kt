package com.example.rent_mobile_app.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.rent_mobile_app.models.user.User
import com.example.rent_mobile_app.utils.EnumUtils.Companion.getUserType
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class UserRepository(private val context: Context) {

    private val TAG = this.toString()
    private val firedb = Firebase.firestore
    private val usersCollection = "users"
    private val fieldId = "id"
    private val fieldName = "name"
    private val fieldEmail = "email"
    private val fieldPassword = "password"
    private val fieldPhone = "phone"
    private val fieldType = "type"
    private val fieldFavoritedProperties = "favoritedProperties"
    private val fieldOwnedProperties = "ownedProperties"

    var users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()

    fun create(newUser: User) {
        //TODO Take appropriate actions if email is already taken
        try {
            val data: MutableMap<String, Any> = HashMap()

            data[fieldId] = newUser.id
            data[fieldName] = newUser.name
            data[fieldEmail] = newUser.email
            data[fieldPassword] = newUser.password
            data[fieldPhone] = newUser.phone
            data[fieldType] = newUser.type
            data[fieldFavoritedProperties] = newUser.favoritedProperties
            data[fieldOwnedProperties] = newUser.ownedProperties

            firedb.collection(usersCollection)
                .document(newUser.id)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, ">>> signupNewUser: Success! $docRef")
                }.addOnFailureListener { ex ->
                    Log.e(TAG, ">>> signupNewUser: Failure! $ex")
                }
        } catch (ex: Exception) {
            Log.e(TAG, ">>> signupNewUser: Exception! $ex")
        }
    }

    fun update(user: User) {
        Log.d(TAG, ">>> updateUser: Success! ${user.toString()}")
        try {
            val data: MutableMap<String, Any> = HashMap()

            data[fieldId] = user.id
            data[fieldName] = user.name
            data[fieldEmail] = user.email
            data[fieldPassword] = user.password
            data[fieldPhone] = user.phone
            data[fieldType] = user.type
            data[fieldFavoritedProperties] = user.favoritedProperties
            data[fieldOwnedProperties] = user.ownedProperties

            firedb.collection(usersCollection)
                .document(user.id)
                .update(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, ">>> updateUser: Success! $docRef")
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, ">>> updateUser: Failure! $ex")
                }
        } catch (ex: Exception) {
            Log.e(TAG, ">>> updateUser: Exception! $ex")
        }
    }

    fun get(userId: String) {
        firedb.collection(usersCollection)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val id = documentSnapshot.getString("id") ?: ""
                    val name = documentSnapshot.getString("name") ?: ""
                    val email = documentSnapshot.getString("email") ?: ""
                    val phone = documentSnapshot.getString("phone") ?: ""
                    val password = documentSnapshot.getString("password") ?: ""
                    val type = documentSnapshot.getString("type") ?: ""
                    val favoritedProperties =
                        documentSnapshot.get("favoritedProperties") as MutableList<String>
                    val ownedProperties =
                        documentSnapshot.get("ownedProperties") as MutableList<String>

                    val user: User = User(
                        id,
                        name,
                        email,
                        password,
                        phone,
                        type,
                        favoritedProperties,
                        ownedProperties
                    )
                    //save to prefs
                    users.postValue(listOf(user))

                    Log.d(TAG, ">>> UserRepository.get: Success! ${user.toString()}")
                } else {
                    Log.e(TAG, ">>> UserRepository.get: Failed! User was not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, ">>> UserRepository.get: Failed! Firestore query failed. $e")
            }
    }

    fun getAll() {
        val data: MutableList<User> = mutableListOf()

        firedb.collection(usersCollection)
            .get()
            .addOnSuccessListener { results ->
                if (results != null) {
                    for (i in results) {
                        data.add(
                            User(
                                i.data.getValue("id").toString(),
                                i.data.getValue("name").toString(),
                                i.data.getValue("email").toString(),
                                i.data.getValue("password").toString(),
                                i.data.getValue("phone").toString(),
                                getUserType(i.data.getValue("type").toString()).name,
                                i.data.getValue("favoritedProperties") as MutableList<String>,
                                i.data.getValue("ownedProperties") as MutableList<String>
                            )
                        )
                    }
                    users.postValue(data)
                } else {
                    Log.e(TAG, ">>> getAll: Failed! User was not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, ">>> getAll: Failed! Firestore query failed. $e")
            }
    }

    fun deleteIfPropertyIsFavorite(targetUsers: MutableList<User>, id: String) {
        Log.d(TAG, ">>> deleteIfPropertyIsFavorite: process start")

        try {
            val users =
                targetUsers.filter { it.favoritedProperties.contains(id) } as MutableList<User>

            if (users != null && users.size > 0) {
                for (i in targetUsers) {
                    i.favoritedProperties.remove(id)
                    update(i)
                    Log.d(
                        TAG,
                        ">>> deleteIfPropertyIsFavorite: favorite property = $id, deleted from user ${i.id}!"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, ">>> delete: Error to delete favorite key = $id!, exception = $e")
        }
    }
}