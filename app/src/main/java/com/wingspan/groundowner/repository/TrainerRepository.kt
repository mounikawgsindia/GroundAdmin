package com.wingspan.groundowner.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.wingspan.groundowner.model.LoginRequest
import com.wingspan.groundowner.model.LoginTrainerResponse
import com.wingspan.groundowner.model.ResponseData
import com.wingspan.groundowner.model.TrainerLoginResponse
import com.wingspan.groundowner.model.TrainerProfileResponse
import com.wingspan.groundowner.model.TrainerRegistrationResponse
import com.wingspan.groundowner.model.VerifyRequest
import com.wingspan.groundowner.network.ApiService
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class TrainerRepository@Inject constructor(var apiserver: ApiService, private var sharedpreferences: UserPreferences,private val contentResolver: ContentResolver) {
    val token = "Bearer ${sharedpreferences.getToken()}"

    //post
    private val _registration = MutableStateFlow<Resource<TrainerRegistrationResponse>?>(null)
    val registration: StateFlow<Resource<TrainerRegistrationResponse>?> = _registration

    //update trainer profile
    private val _updateProfile = MutableStateFlow<Resource<TrainerProfileResponse>?>(null)
    val updateProfile: StateFlow<Resource<TrainerProfileResponse>?> = _updateProfile

    //login
    private val _loginTrainer = MutableStateFlow<Resource<ResponseData>?>(null)
    val loginTrainer: StateFlow<Resource<ResponseData>?> = _loginTrainer

    //resend
    private val _resendTrainer = MutableStateFlow<Resource<ResponseData>?>(null)
    val resendTrainer: StateFlow<Resource<ResponseData>?> = _resendTrainer

    //verify
    private val _verifyTrainer = MutableStateFlow<Resource<TrainerLoginResponse>?>(null)
    val verifyTrainer: StateFlow<Resource<TrainerLoginResponse>?> = _verifyTrainer


    //verify register
    private val _verifyRegisterTrainer = MutableStateFlow<Resource<LoginTrainerResponse>?>(null)
    val verifyRegisterTrainer: StateFlow<Resource<LoginTrainerResponse>?> = _verifyRegisterTrainer


    suspend fun loginTrainer(number: String) {
        Log.d("loginTrainer ","--->${token}")
        safeApiCall(_loginTrainer) { apiserver.loginTrainer(LoginRequest(number)) }
    }
//after login
    suspend fun verifyTrainer(pin: String, mobileNumber: String) {
        Log.d("verifyTrainer ","--->${pin}...${mobileNumber}")
        safeApiCall(_verifyTrainer) { apiserver.verifyTrainerNumber(VerifyRequest(mobileNumber,pin)) }
    }

    suspend fun verifyRegsiterTrainer(pin: String, mobileNumber: String) {
        Log.d("verifyTrainer ","--->${pin}...${mobileNumber}")
        safeApiCall(_verifyRegisterTrainer) { apiserver.verifyTrainerRegisterNumber(VerifyRequest(mobileNumber,pin)) }
    }

    suspend fun resendTrainer(mobileNumber: String) {
        var number= "+91$mobileNumber"
        Log.d("resendTrainer ","--->${number}")
        safeApiCall(_resendTrainer) { apiserver.resendTrainer(LoginRequest(number)) }
    }


    suspend fun postRegistration(  context: Context,
        phoneNumber: String,
        email: String,
        fullName: String,
        specialization: String,
        addImagesList: ArrayList<Uri>,
        personalImage: Uri?
    ) {
        Log.d("postRegistration ","--->${token}")
        val phoneBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
        val specializationBody = specialization.toRequestBody("text/plain".toMediaTypeOrNull())

        val personalImagePart: MultipartBody.Part? = personalImage?.let { uri ->
            val mimeType = context.contentResolver.getType(uri) ?: "image/*"
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return@let null
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                "profileImage",
                "profile_${System.currentTimeMillis()}.jpg",
                requestBody
            )
        }



        val imageParts = addImagesList.map { uri ->
            val file = File(getRealPathFromURI(uri)) // Use appropriate function to get real path from URI
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("galleryImages", file.name, requestFile)
        }

        safeApiCall(_registration) { apiserver.RegistrationTrainer(phoneBody,fullNameBody,specializationBody,emailBody,imageParts,personalImagePart) }
    }

    suspend fun updateTrainerProfile(
        context: Context,
        address: String,
        about: String,
        pricing: String,
        slots: String,
        specialization: String,
        fullName: String,
        addImagesList: ArrayList<Uri>,
        personalImage: Uri?,
        selectedDaysList: ArrayList<String>
    ) {
        Log.d("updateTrainerProfile", "---> $token")

        val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val aboutBody = about.toRequestBody("text/plain".toMediaTypeOrNull())
        val pricingBody = pricing.toRequestBody("text/plain".toMediaTypeOrNull())
        val slotsBody = slots.toRequestBody("text/plain".toMediaTypeOrNull())
        val specializationBody = specialization.toRequestBody("text/plain".toMediaTypeOrNull())
        val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())

        val daysParts = selectedDaysList.map {
            it.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val personalImagePart: MultipartBody.Part? = personalImage?.let { uri ->
            try {
                val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@let null
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "profileImage",
                    "profile_${System.currentTimeMillis()}.jpg",
                    requestBody
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        val imageParts = addImagesList.mapNotNull { uri ->
            try {
                val mimeType = context.contentResolver.getType(uri) ?: "image/*"
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@mapNotNull null
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "galleryImages",
                    "gallery_${System.currentTimeMillis()}.jpg",
                    requestBody
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        safeApiCall(_updateProfile) {
            apiserver.UpdateTrainer(
                token,
                addressBody,
                fullNameBody,
                specializationBody,
                aboutBody,
                pricingBody,
                slotsBody,
                imageParts,
                personalImagePart
            )
        }
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
    fun getRealPathFromURI(contentUri: Uri?): String? {
        val cursor = contentResolver.query(contentUri!!, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            if (columnIndex != -1 && it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }

}