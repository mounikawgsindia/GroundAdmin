package com.wingspan.groundowner.network

import com.wingspan.groundowner.model.BookingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface OwnerApiService {

    @GET("api/payments/owner/bookings")
    suspend fun groundBookingd(@Header("Authorization")token:String): Response<BookingResponse>
}