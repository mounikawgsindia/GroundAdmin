package com.wingspan.groundowner.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

import kotlinx.parcelize.Parcelize

data class LoginRequest(
    val phoneNumber: String?
)
data class LoginResponse(
    val success: Boolean,
    val token: String?,  // Token received on successful login
    val message: String?,
    val ground:GroundOwner// Error message (if login fails)
)

data class LoginTrainerResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("token") val token: String?
)
data class GroundOwner(
    val id: Int,
    val username: String,
    val email: String,
    val phoneNumber: String,
     val isPro: Boolean)
data class RegisterRequest(
    val username: String,
    val email: String,
    val phoneNumber: String
)
data class CityResponse(
    val message: String?,var cities:List<Cities>
)

data class AreaResponse(
    val message: String,
    val areas: List<Area>
)
data class Area(
    val id: Int,
    val name: String,
    val cityId: Int,
    val cityName: String
)
data class Cities(var id:String?,var name:String?)

data class VerifyRequest(
    val phoneNumber: String?,val otp:String?
)

data class RegisterResponse(
    val success: Boolean,
    val message: String?
)
data class Ground(
    val id: Int?,
    val name: String?,
    val location: String?,
    val imageUrl: String?,
    val capacity: Int?,
    val isAvailable: Boolean?
)
data class GroundResponse(
    val data: List<Ground>,
    val totalRecords: Int
)

data class ResponseData(var message:String?)

data class GetGroundsResponse(
    val message: String,
    val grounds: List<GetGround>
)

data class DeleteResponse(val message:String?)
data class PostGroundsResponse(
    val message: String,
    val ground: GetGround
)

data class GetGround(
    val id: Int,
    val userId: Int,
    val phoneNumber: String,
    val imageUrl: List<String>,
    val groundLocationLink: String,
    val groundAddress: String,
    val groundHeading: String,
    val facilities: String,
    val sportsType: String,
    val aboutVenue: String,
    val venueRules: String,
    val createdAt: String,
    val cityName: String?,
    val areaName: String?,
    val cityId: Int?,
    val areaId: Int?,
    val pincode: String?,
    val slots: List<String>?,
    val pricePerHour: Int
):Serializable
data class Slot(
    val start: String,
    val end: String
)
data class ImageUrl(
    val name: String
)



data class BookingResponse(
    val success: Boolean,
    val message: String,
    val data: BookingData
)

data class BookingData(
    val activeBookings: List<Booking>,
    val canceledBookings: List<CanceledBooking>,
    val totalActive: Int,
    val totalCanceled: Int
)



@Parcelize
data class Ground1(
    val id: Int,
    val groundHeading: String,
    val groundAddress: String
) : Parcelable


@Parcelize
data class CanceledBooking(
    val id: Int,
    val slots: String,
    val totalAmount: Int,
    val refundAmount: Int,
    val sport: String,
    val date: String,
    val cancellationDate: String,
    val status: String
) : Parcelable

@Parcelize
data class Booking(
    val id: Int,
    val user: User,
    val ground: Ground1?,
    val slots: String, // Can be parsed further to List<String> if needed
    val totalAmount: Int,
    val sport: String,
    val status: String,
    val date: String,
    val createdAt: String
) : Parcelable

@Parcelize
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val phoneNumber: String
) : Parcelable

data class TrainerRegistrationResponse(
    val success: Boolean,
    val message: String,
    val gallery: List<String>
)
data class TrainerLoginResponse(val success: Boolean,
                                   val message: String,
                                   val token: String,
                                   val data: UserData1)
data class UserData1(
    val id: Int,
    val phoneNumber: String,
    val fullName: String,
    val email: String,
    val specialization: String,
    val profileImage: String,
    val gallery: List<String>,
    val isVerified: Boolean
)

data class TrainerProfileResponse(
    val message: String,
    val trainer: Trainer
)

data class Trainer(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val email: String,
    val specialization: String,
    val days: List<String>,
    val address: String,
    val about: String?,        // Nullable
    val pricing: String?,      // Nullable
    val slots: String?,        // Nullable
    val profileImage: String?, // Nullable (URL or null)
    val gallery: List<String>,
    val otp: String,
    val otpExpires: String?,   // Nullable
    val isVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

