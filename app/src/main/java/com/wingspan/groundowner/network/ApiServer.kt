package com.wingspan.groundowner.network



import com.wingspan.groundowner.model.AreaResponse
import com.wingspan.groundowner.model.BookingResponse
import com.wingspan.groundowner.model.CityResponse
import com.wingspan.groundowner.model.DeleteResponse
import com.wingspan.groundowner.model.GetGroundsResponse
import com.wingspan.groundowner.model.LoginRequest
import com.wingspan.groundowner.model.LoginResponse
import com.wingspan.groundowner.model.PostGroundsResponse
import com.wingspan.groundowner.model.RegisterRequest
import com.wingspan.groundowner.model.ResponseData
import com.wingspan.groundowner.model.VerifyRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    //login with phonenumber
    @POST("ground/send-login-otp")
    suspend fun loginAdmin(@Body request:LoginRequest): Response<ResponseData>

    //verify
    @POST("ground/verify-otp")
    suspend fun verifyNumber(@Body request:VerifyRequest): Response<LoginResponse>

    //verify
    @POST("ground/resend-otp")
    suspend fun resend(@Body request:LoginRequest): Response<ResponseData>

    @POST("ground/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseData>


    //ground add
    @POST("register")
    suspend fun addGround(@Body request: RegisterRequest): Response<ResponseData>

    //city
    @GET("cities/cities")
    suspend fun getCity(): Response<CityResponse>


    //area
    @GET("area/city/{id}")
    suspend fun getArea(@Path("id") cityId: Int): Response<AreaResponse>

    //get ground data
    @GET("owner/images")
    suspend fun getGroundData(@Header("Authorization")token:String): Response<GetGroundsResponse>

    //delete ground data
    @DELETE("owner/images/{imageId}")
    suspend fun deleteGround(@Header("Authorization")token:String,@Path("imageId")groundId:Int): Response<DeleteResponse>


    //post ground images
    @Multipart
    @POST("owner/multiple")
    suspend fun postGroundDetails(
        @Header("Authorization") token: String,
        @Part("pricePerHour") pricePerHour: RequestBody,
        @Part("pincode") pincode: RequestBody,
        @Part("areaId") areaId: RequestBody,
        @Part("cityId") cityId: RequestBody,
        @Part("venueRules") venueRules: RequestBody,
        @Part("aboutVenue") aboutVenue: RequestBody,
        @Part("sportsType") sportsType: RequestBody,
        @Part("facilities") facilities: RequestBody,
        @Part("groundHeading") groundHeading: RequestBody,
        @Part("groundAddress") groundAddress: RequestBody,
        @Part("groundLocationLink") groundLocationLink: RequestBody,
        @Part("imageName") imageName: RequestBody,
        @Part("slots") slots: RequestBody,
        @Part images: List<MultipartBody.Part>,

    ): Response<PostGroundsResponse>


    @GET("ground/booking-summary")
    suspend fun groundBooking(@Header("Authorization")token:String): Response<BookingResponse>
}