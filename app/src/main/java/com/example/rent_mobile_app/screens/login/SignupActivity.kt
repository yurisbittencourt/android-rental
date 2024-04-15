package com.example.rent_mobile_app.screens.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import com.example.rent_mobile_app.databinding.ActivitySignupBinding
import com.example.rent_mobile_app.models.user.User
import com.example.rent_mobile_app.models.user.UserTypeEnum
import com.example.rent_mobile_app.repositories.UserRepository
import com.example.rent_mobile_app.utils.MessageUtils.Companion.snack
import com.example.rent_mobile_app.utils.SuperMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignupActivity : SuperMain() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var userRepository: UserRepository
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = this.toString()
    private var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbar)

        userRepository = UserRepository(this)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener() {
            createNewUser()
        }
        binding.btnLogin.setOnClickListener() {
            redirectLogin()
        }
        setUserTypeSpinner()
    }

    private fun createNewUser() {
        val firstNameInput = binding.etFirstName.text.toString()
        val lastNameInput = binding.etLastName.text.toString()
        val emailInput = binding.etEmail.text.toString()
        val passwordInput = binding.etPassword.text.toString()
        val confirmPasswordInput = binding.etConfirmPassword.text.toString()

        val newUserIsValid: Boolean = inputsAreValid(
            firstNameInput,
            lastNameInput,
            emailInput,
            passwordInput,
            confirmPasswordInput
        )

        if (newUserIsValid) {
            firebaseAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signUp: signing up process")
                        val userId = firebaseAuth.currentUser?.uid
                        //Create firestore user document with uid
                        userId?.let {
                            val newUser =
                                User(
                                    userId,
                                    "$firstNameInput $lastNameInput",
                                    emailInput,
                                    passwordInput,
                                    "",
                                    binding.spinnerSignUpUserType.selectedItem.toString(),
                                    mutableListOf<String>(),
                                    mutableListOf<String>()
                                )
                            userRepository.create(newUser)
                            redirectLogin()
                        }
                    } else {
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            message = "Email already exists!"
                        } else {
                            message = "Authentication failed!"
                        }

                        Log.e(TAG, "signUp: $message", task.exception)
                        snack(binding.root, message)
                    }
                }
        } else {
            snack(binding.root, "Please fill out the fields appropriately")
        }
    }

    private fun inputsAreValid(
        firstNameInput: String,
        lastNameInput: String,
        emailInput: String,
        passwordInput: String,
        confirmPasswordInput: String
    ): Boolean {
        var valid = true

        if (firstNameInput == "") {
            binding.etFirstName.error = "Must input first name"
            valid = false
        }

        if (lastNameInput == "") {
            binding.etLastName.error = "Must input last name"
            valid = false
        }

        if (emailInput == "") {
            binding.etEmail.error = "Must input email"
            valid = false
        }

        if ((passwordInput == "") || (passwordInput.length < 6)) {
            binding.etPassword.error = "Must input password with minimum of 6 characters"
            valid = false
        }

        if ((confirmPasswordInput == "") || (confirmPasswordInput.length < 6)) {
            binding.etConfirmPassword.error =
                "Must input password confirmation with minimum of 6 characters"
            valid = false
        }

        if (passwordInput != confirmPasswordInput) {
            binding.etConfirmPassword.error = "Must match password"
            valid = false
        }

        val emailPattern = Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(emailInput).matches()) {
            binding.etEmail.error = "Must be a valid email"
            valid = false
        }

        return valid
    }

    private fun setUserTypeSpinner() {
        val userTypes: MutableList<UserTypeEnum> = mutableListOf()
        userTypes.add(UserTypeEnum.TENANT)
        userTypes.add(UserTypeEnum.LANDLORD)

        val userTypesAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, userTypes.map { it.name })

        binding.spinnerSignUpUserType.adapter = userTypesAdapter
    }
}