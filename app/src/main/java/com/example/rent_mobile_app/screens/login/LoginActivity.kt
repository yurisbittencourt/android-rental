package com.example.rent_mobile_app.screens.login

import android.os.Bundle
import android.util.Log
import com.example.rent_mobile_app.databinding.ActivityLoginBinding
import com.example.rent_mobile_app.models.user.User
import com.example.rent_mobile_app.repositories.UserRepository
import com.example.rent_mobile_app.utils.MessageUtils.Companion.snack
import com.example.rent_mobile_app.utils.StorageActions
import com.example.rent_mobile_app.utils.SuperMain
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : SuperMain() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var users: List<User>
    private lateinit var storageActions: StorageActions
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    private var TAG = this.toString()

    //TODO Automatically login user based on shared prefs || based on Firebase Auth: https://firebase.google.com/docs/auth/android/start
    //TODO Give more descriptive auth errors? Incorrect email / password
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(binding.toolbar)

        storageActions = StorageActions(this)
        userRepository = UserRepository(this)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener() {
            loginUser()
        }

        binding.btnRegister.setOnClickListener() {
            redirectSignUp()
        }
    }

    private fun loginUser() {
        val emailInput = binding.etEmail.text.toString()
        val passwordInput = binding.etPassword.text.toString()

        firebaseAuth.signInWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, ">>> loginUser: Success!")
                    //get user document from uid and set it as current user
                    val userId = firebaseAuth.currentUser?.uid
                    userId?.let {
                        userRepository.get(it)
                    }
                } else {
                    Log.e(TAG, ">>> loginUser: Failure! ${task.exception}")
                    snack(binding.root, "Oops! ${task.exception}")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        userRepository.users.observe(this, androidx.lifecycle.Observer { result ->
            if (result != null) {
                storageActions.setCurrentUser(result[0])
                currentUser = result[0]
                redirectHome()
            }
        })
    }
}