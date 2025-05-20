package com.wingspan.groundowner.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.wingspan.groundowner.model.TrainerRegistrationResponse
import com.wingspan.groundowner.network.ApiService
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class TrainerRepository@Inject constructor(var apiserver: ApiService, private var sharedpreferences: UserPreferences,private val contentResolver: ContentResolver) {
    val token = "Bearer ${sharedpreferences.getToken()}"

    //get
    private val _registration = MutableStateFlow<Resource<TrainerRegistrationResponse>?>(null)
    val registration: StateFlow<Resource<TrainerRegistrationResponse>?> = _registration


    suspend fun postRegistration(
        phoneNumber: String,
        email: String,
        fullName: String,
        specialization:String,
        addImagesList: ArrayList<Uri>
    ) {
        Log.d("registration ","--->${token}")
        val phoneBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val specializationBody = specialization.toRequestBody("text/plain".toMediaTypeOrNull())


        val imageParts = addImagesList.map { uri ->
            val file = File(getRealPathFromURI(uri)) // Use appropriate function to get real path from URI
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("imageFiles", file.name, requestFile)
        }

        safeApiCall(_registration) { apiserver.RegistrationTrainer(phoneBody,fullNameBody,specializationBody,emailBody,imageParts) }
    }

    suspend fun <T> safeApiCall(
        state: MutableStateFlow<Resource<T>?>,
        apiCall: suspend () -> Response<T>
    ) {
        state.value = Resource.Loading<T>()
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("response ","--->${body}")
                if (body != null) {
                    state.value = Resource.Success(body)
                } else {
                    state.value = Resource.Error("Empty response")
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMessage = parseErrorMessage(errorBody) ?: "Unexpected error occurred"
                Log.e("ApiCall Error", "--Code: ${response.code()}, Message: $errorMessage")


                Log.d("error try else  response repository ","--->${errorMessage}")
                state.value = Resource.Error("${errorMessage}")
            }
        } catch (e: Exception) {
            Log.d("error catch response repository ","--->${e.message}")
            state.value = Resource.Error(e.localizedMessage ?: "Unknown error")
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