package com.wingspan.groundowner.network


import com.wingspan.groundowner.model.LoginRequest
import com.wingspan.groundowner.model.LoginResponse
import com.wingspan.groundowner.model.RegisterRequest
import com.wingspan.groundowner.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>


    //login
    @POST("auth/signinResponse")
    @Headers("Content-Type:application/json")
    suspend fun loginAdmin(@Body loginData: LoginRequest): Response<LoginResponse>
}