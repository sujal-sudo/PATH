package com.example.path.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.path.R
import com.example.path.databinding.ActivitySignUpBinding
import com.example.path.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {


    // Firebase connection
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivitySignUpBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)

//        setContentView(R.layout.activity_sign_up) // Ensure this layout exists
        setContentView(binding.root) // Ensure this layout exists


        //Firebase and Database
        firebaseDatabase = FirebaseDatabase.getInstance()// initializing database
        databaseReference = firebaseDatabase.reference.child("users")
        // here it will use database through databaserefrecnce
        binding.signupButton.setOnClickListener {
            val username = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signupUser(username, email, password)
            } else {
                Toast.makeText(this@SignUpActivity, "All fields are mandatory ", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        binding.loginTextview.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }

        // Initialize UI elements
        val signupButton: Button = findViewById(R.id.signup_button)
        val termsCheckbox: CheckBox = findViewById(R.id.terms_checkbox)
        val emailEditText: EditText = findViewById(R.id.signup_email)
        val nameEditText: EditText = findViewById(R.id.signup_name)
        val passwordEditText: EditText = findViewById(R.id.signup_password)
        val loginTextview: TextView = findViewById(R.id.loginTextview)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()



        // Set click listener for the Login TextView
        loginTextview.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }



        // Set click listener for the Constraint layout for hiding keyboard
        binding.myConstraintLayout.setOnTouchListener { _, _ ->
            hideKeyboardAndClearFocus()
            true // Consume the touch event
        }
    }
    // This is the database Fucntion that checks the required fucntion to run the database
    private fun signupUser(username: String, password: String, email: String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { // snapshot is the object that specifies a particular location of a data
                if (!snapshot.exists()){
                    val id = databaseReference.push().key
                    val userData = UserData(id,username, email)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@SignUpActivity, "Signup Successful" , Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
                    finish()
                } else{
                    Toast.makeText(this@SignUpActivity, "User Already Exists" , Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@SignUpActivity, "Database Error: ${databaseError.message}" , Toast.LENGTH_SHORT).show()


            }
        })
    }




    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(Exception::class.java)
            account?.let {
                Log.d("GoogleSignIn", "Sign-in successful. Name: ${it.displayName}, Email: ${it.email}")
                Toast.makeText(this, "Signed in as: ${it.email}", Toast.LENGTH_SHORT).show()
                // Add logic for successful sign-in (e.g., navigate to the main activity)
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign-in failed", e)
            Toast.makeText(this, "Sign-in failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboardAndClearFocus() {
        // Hide the keyboard
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        // Clear focus from the currently focused view
        currentFocus?.clearFocus()

        //  set focus to the layout
        binding.myConstraintLayout.requestFocus()
    }
}
