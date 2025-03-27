package com.wingspan.groundowner.repository

import android.util.Log

import com.wingspan.groundowner.model.LoginRequest
import com.wingspan.groundowner.model.LoginResponse
import com.wingspan.groundowner.model.RegisterRequest
import com.wingspan.groundowner.model.RegisterResponse

import com.wingspan.groundowner.network.ApiService

import com.wingspan.groundowner.utils.Resource
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService1: ApiService)  {


        suspend fun login(mobileNumber: String): Resource<LoginResponse> {
            Log.d("Login", "Attempting login with mobile: $mobileNumber")
            return safeApiCall {
                apiService1.loginAdmin(LoginRequest(mobileNumber))
            }
        }

        suspend fun register(name: String, email: String, password: String): Resource<RegisterResponse> {
            Log.d("Register", "Attempting registration with email: $email")
            return safeApiCall {
                apiService1.register(RegisterRequest(name, email, password))
            }
        }

        // Centralized API call handler
        private inline fun <T> safeApiCall(apiCall: () -> Response<T>): Resource<T> {
            return try {
                val response = apiCall()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.Success(it)
                    } ?: Resource.Error("Empty response body")
                } else {
                    val errorMessage = parseError(response.errorBody()?.string())
                    Log.e("API Error", "Code: ${response.code()}, Message: $errorMessage")
                    Resource.Error(errorMessage)
                }
            } catch (e: IOException) {
                Log.e("Network Error", "IOException: ${e.message}")
                Resource.Error("Network connection issue, please check your internet")
            } catch (e: retrofit2.HttpException) {
                Log.e("Server Error", "HttpException: ${e.message}")
                Resource.Error("Server error, please try again later")
            } catch (e: Exception) {
                Log.e("Unexpected Error", "Exception: ${e.message}")
                Resource.Error("Unexpected error occurred: ${e.message}")
            }
        }

        // Parses the error response body
        private fun parseError(errorBodyString: String?): String {
            return try {
                errorBodyString?.let {
                    val jsonObject = JSONObject(it)
                    jsonObject.optString("error")
                        .takeIf { msg -> msg.isNotBlank() }
                        ?: jsonObject.optString("message")
                            .takeIf { msg -> msg.isNotBlank() }
                        ?: "An unexpected error occurred"
                } ?: "Unknown error occurred"
            } catch (e: Exception) {
                Log.e("JSONParsingError", "Failed to parse error: ${e.message}")
                "Failed to parse error: ${e.message}"
            }
        }
}

