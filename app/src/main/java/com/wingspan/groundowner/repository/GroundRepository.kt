package com.wingspan.groundowner.repository

import AreaResponse
import CityResponse
import DeleteResponse
import GetGroundsResponse
import PostGroundsResponse
import Slot
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.gson.Gson
import com.wingspan.groundowner.network.ApiService
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.UserPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class GroundRepository @Inject constructor(private val apiService1: ApiService,private var sharedpreferences:UserPreferences,private val contentResolver: ContentResolver)  {

    suspend fun getCity(): Resource<CityResponse> {
        Log.d("getCity","--->getCity repository ")
        return try {
            val response = apiService1.getCity()
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
    suspend fun getArea(id: String): Resource<AreaResponse> {
        Log.d("getCity","--->getCity repository ")
        return try {
            val response = apiService1.getArea(id.toInt())
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

    suspend fun getGroundData(): Resource<GetGroundsResponse> {

        val token=sharedpreferences.getToken()
        val tokenVarible="Bearer ${token}"
        Log.d("getGroundData","--->getGroundData repository ${tokenVarible}")
        return try {
            val response = apiService1.getGroundData(tokenVarible)
            //response.isSuccessful means 200 to 299
            if (response.isSuccessful) {
                Log.d("getGroundData","--->getGroundData repository isSuccessful ${response.body()}")
                Resource.Success(response.body()!!)
            } else {

                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("getGroundData Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: SocketTimeoutException) {
            Resource.Error("Server timeout, please try again later")
        } catch (e: UnknownHostException) {
            Resource.Error("No internet connection, please check your network")
        } catch (e: HttpException) {
            Resource.Error("Server error, please try again later")
        } catch (e: IOException) {
            Resource.Error("Network connection issue, please check your internet")
        }
    }

    suspend fun deleteGround(id:Int): Resource<DeleteResponse> {

        val token=sharedpreferences.getToken()
        val tokenVarible="Bearer ${token}"
        Log.d("deleteGround","--->deleteGround repository ${tokenVarible}")
        return try {
            val response = apiService1.deleteGround(tokenVarible,id)
            //response.isSuccessful means 200 to 299
            if (response.isSuccessful) {
                Log.d("deleteGround","--->deleteGround repository isSuccessful ${response.body()}")
                Resource.Success(response.body()!!)
            } else {

                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("deleteGround Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: SocketTimeoutException) {
            Resource.Error("Server timeout, please try again later")
        } catch (e: UnknownHostException) {
            Resource.Error("No internet connection, please check your network")
        } catch (e: HttpException) {
            Resource.Error("Server error, please try again later")
        } catch (e: IOException) {
            Resource.Error("Network connection issue, please check your internet")
        }
    }

    suspend fun postGroundData(
        priceperhour: String,
        venuerules: String,
        venueet: String,
        facilitieset: String,
        pincodeet: String,
        areaId: String,
        cityId: String,
        maplinket: String,
        groundaddresset: String,
        sportstypeet: String,
        groundheadinget: String,
        imageNamesList: ArrayList<String>,
        addImagesList: ArrayList<Uri>,
        selectedTimeSlots: MutableList<Slot>
    ): Resource<PostGroundsResponse> {

        val token=sharedpreferences.getToken()
        val tokenVarible="Bearer ${token}"
        Log.d("getGroundData","--->getGroundData repository ${tokenVarible}...$imageNamesList")
        return try {
            // Create MultipartBody.Part for images
            val imageParts = addImagesList.map { uri ->
                val file = File(getRealPathFromURI(uri)) // Use appropriate function to get real path from URI
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            }

            // Create RequestBody for other fields
            val pricePerHour = priceperhour.toRequestBody("text/plain".toMediaTypeOrNull())
            val venueRules = venuerules.toRequestBody("text/plain".toMediaTypeOrNull())
            val aboutVenue = venueet.toRequestBody("text/plain".toMediaTypeOrNull())
            val facilities = facilitieset.toRequestBody("text/plain".toMediaTypeOrNull())
            val pincode = pincodeet.toRequestBody("text/plain".toMediaTypeOrNull())
            val area = areaId.toRequestBody("text/plain".toMediaTypeOrNull())
            val city = cityId.toRequestBody("text/plain".toMediaTypeOrNull())
            val locationLink = maplinket.toRequestBody("text/plain".toMediaTypeOrNull())
            val groundAddress = groundaddresset.toRequestBody("text/plain".toMediaTypeOrNull())
            val sportsType = sportstypeet.toRequestBody("text/plain".toMediaTypeOrNull())
            val groundHeading = groundheadinget.toRequestBody("text/plain".toMediaTypeOrNull())


            val gson = Gson()


            val jsonSlots = gson.toJson(selectedTimeSlots)

            val slotsRequestBody = jsonSlots.toRequestBody("application/json".toMediaTypeOrNull())
            val response = apiService1.postGroundDetails(tokenVarible, pricePerHour, pincode, area,
                city, venueRules, aboutVenue, sportsType, facilities, groundHeading, groundAddress, locationLink,
                imageNamesList.first().toRequestBody("text/plain".toMediaTypeOrNull()), // Assuming one image name per API call
                slotsRequestBody, // Assuming you want to send time slots as JSON
                imageParts
            )
            //response.isSuccessful means 200 to 299
            if (response.isSuccessful) {
                Log.d("getGroundData","--->getGroundData repository isSuccessful ${response.body()}")
                Resource.Success(response.body()!!)
            } else {

                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.d("getGroundData Error", "Response Code: ${response.code()}, Error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Log.d("error exp","--->ground ${e.message}")
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
    fun getRealPathFromURI(contentUri: Uri): String? {
        val cursor = contentResolver.query(contentUri, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            if (columnIndex != -1 && it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }


}