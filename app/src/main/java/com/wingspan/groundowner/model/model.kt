import android.os.Parcelable
import java.io.Serializable

import kotlinx.parcelize.Parcelize
data class LoginRequest(
    val phoneNumber: String
)
data class LoginResponse(
    val success: Boolean,
    val token: String?,  // Token received on successful login
    val message: String?,
    val ground:GroundOwner// Error message (if login fails)
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

//@Parcelize
//data class BookingModel(
//    val user: String,
//    val ground: String,
//    val bookingId: String,
//    val slots: String,
//    val amount: String,
//    val sports: String,
//    val status: String,
//    val date: String,
//    val created: String
//): Parcelable

data class BookingResponse(
    val success: Boolean,
    val bookings: List<Booking>
)
@Parcelize
data class Booking(
    val id: Int,
    val userId: Int,
    val groundId: Int,
    val ownerId: Int,
    val PhoneNumber: String,
    val slots: String, // or List<String> if you parse it further
    val totalAmount: Int,
    val sport: String,
    val status: String,
    val date: String,
    val createdAt: String,
    val updatedAt: String,
    val user: User
): Parcelable
@Parcelize


data class User(
    val id: Int,
    val username: String,
    val email: String,
    val phoneNumber: String
): Parcelable