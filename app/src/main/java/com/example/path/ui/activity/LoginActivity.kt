package com.example.path.ui.activity

import android.adservices.ondevicepersonalization.UserData
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.path.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting up the database
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isConnectedToInternet()) {
                    loginUser(email, password)
                } else {
                    showNoInternetError()
                }
            } else {
                Toast.makeText(this@LoginActivity, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        binding.myConstraintLayout.setOnTouchListener { _, _ ->
            hideKeyboardAndClearFocus()
            true // Consume the touch event
        }

//        binding.textViewForgotPassword.setOnClickListener {
//            startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
//
//        }

        binding.textViewSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))

        }
    }

    private fun hideKeyboardAndClearFocus() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        currentFocus?.clearFocus()
        binding.myConstraintLayout.requestFocus()
    }

    private fun loginUser(email: String, password: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userData = userSnapshot.getValue(UserData::class.java)

                            Log.d("LoginDebug", "Fetched UserData: $userData") // Debugging Log

//                            if (userData != null && userData.email == email && userData.password == password) {
//                                Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
//                                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
//                                finish()
//                                return
//                            }
                        }
                    }

                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginDebug", "Database Error: ${databaseError.message}") // Debugging Log
                }
            })
    }


    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showNoInternetError() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("No Internet Connection")
        builder.setMessage("Please check your internet connection and try again.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close the dialog
        }
        builder.setCancelable(false) // Prevent dismissing the dialog by tapping outside
        val dialog = builder.create()
        dialog.show()
    }

}
