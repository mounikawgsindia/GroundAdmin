package com.wingspan.groundowner.repository

import com.wingspan.groundowner.model.LoginRequest
import com.wingspan.groundowner.model.LoginResponse
import com.wingspan.groundowner.model.RegisterRequest
import com.wingspan.groundowner.model.ResponseData
import com.wingspan.groundowner.model.VerifyRequest
import android.util.Log


import com.wingspan.groundowner.network.ApiService

import com.wingspan.groundowner.utils.Resource
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class AuthRepository @Inject constructor(private val apiService1: ApiService)  {



    suspend fun login(mobilenumber:String): Resource<ResponseData> {
        Log.d("Login","--->login repository $mobilenumber")
        return try {
            val response = apiService1.loginAdmin(LoginRequest(mobilenumber))
            //response.isSuccessful means 200 to 299
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {

                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("Login Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.d("error exp","--->login ${e.message}")
            Resource.Error("Network Error: ${e.message}")
        }catch (e: SocketTimeoutException) {
            Resource.Error("Server timeout, please try again later")
        } catch (e: UnknownHostException) {
            Resource.Error("No internet connection, please check your network")
        } catch (e: HttpException) {
            Resource.Error("Server error, please try again later")
        } catch (e: IOException) {
            Resource.Error("Network connection issue, please check your internet")
        }
    }

    suspend fun register(name: String, email: String, mobilenumber: String): Resource<ResponseData> {
        return try {
            Log.d("register","--->login repository $mobilenumber..$name...$email")
            val response = apiService1.register(RegisterRequest(name, email, mobilenumber))
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("register Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.d("error exp","--->login ${e.message}")
            Resource.Error("Network Error: ${e.message}")
        }catch (e: SocketTimeoutException) {
            Resource.Error("Server timeout, please try again later")
        } catch (e: UnknownHostException) {
            Resource.Error("No internet connection, please check your network")
        } catch (e: HttpException) {
            Resource.Error("Server error, please try again later")
        } catch (e: IOException) {
            Resource.Error("Network connection issue, please check your internet")
        }
    }

    suspend fun verifyMobileNumber(pin: String, mobilenumber: String): Resource<LoginResponse> {
        return try {
            val mobileNumber="+91$mobilenumber"
            Log.d("verifyMobileNumber","--->verifyMobileNumber repository $mobileNumber..$pin")
            val response = apiService1.verifyNumber(VerifyRequest(mobileNumber,pin))
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {

                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("Login Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.d("error exp","--->login ${e.message}")
            Resource.Error("Network Error: ${e.message}")
        }catch (e: SocketTimeoutException) {
            Resource.Error("Server timeout, please try again later")
        } catch (e: UnknownHostException) {
            Resource.Error("No internet connection, please check your network")
        } catch (e: HttpException) {
            Resource.Error("Server error, please try again later")
        } catch (e: IOException) {
            Resource.Error("Network connection issue, please check your internet")
        }
    }


    suspend fun resend(mobilenumber: String?): Resource<ResponseData> {
        return try {
            val response = apiService1.resend(LoginRequest(mobilenumber!!))
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {

                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("Login Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.d("error exp","--->login ${e.message}")
            Resource.Error("Network Error: ${e.message}")
        }catch (e: SocketTimeoutException) {
            Resource.Error("Server timeout, please try again later")
        } catch (e: UnknownHostException) {
            Resource.Error("No internet connection, please check your network")
        } catch (e: HttpException) {
            Resource.Error("Server error, please try again later")
        } catch (e: IOException) {
            Resource.Error("Network connection issue, please check your internet")
        }
    }
    // Helper function to parse error message
    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.optString("message", null)  // Extract "message" from the error body
        } catch (e: JSONException) {
            null
        }
    }
}


