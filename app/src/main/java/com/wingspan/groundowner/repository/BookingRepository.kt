package com.wingspan.groundowner.repository

import BookingResponse
import android.util.Log
import com.wingspan.groundowner.network.ApiService
import com.wingspan.groundowner.network.OwnerApiService
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class BookingRepository@Inject constructor(var apiserver: ApiService, private var sharedpreferences: UserPreferences) {


    val token = "Bearer ${sharedpreferences.getToken()}"

    //get
    private val _getbookings = MutableStateFlow<Resource<BookingResponse>?>(null)
    val getbookings: StateFlow<Resource<BookingResponse>?> = _getbookings


    suspend fun groundBooking() {
        Log.d("groundBooking ","--->${token}")
        safeApiCall(_getbookings) { apiserver.groundBooking(token) }
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
}