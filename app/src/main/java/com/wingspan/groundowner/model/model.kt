package com.wingspan.groundowner.model

data class LoginRequest(
    val mobileNumber: String,

)
data class LoginResponse(
    val success: Boolean,
    val token: String?,  // Token received on successful login
    val message: String? // Error message (if login fails)
)
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
data class RegisterResponse(
    val success: Boolean,
    val message: String?
)
