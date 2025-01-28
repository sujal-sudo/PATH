package com.example.path.model

data class UserData(
    // storing 3 data in realtime database
    val id: String? = null,
    val email: String = "",
    val password: String = "",
   // List to hold transaction IDs
)