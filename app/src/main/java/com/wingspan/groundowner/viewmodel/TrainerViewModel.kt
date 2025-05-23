package com.wingspan.groundowner.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.groundowner.repository.TrainerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainerViewModel @Inject constructor(var repository : TrainerRepository) : ViewModel() {

    val postRegistration = repository.registration

    val loginTrainer=repository.loginTrainer
    val verifyTrainer=repository.verifyTrainer
    val verifyRegisterTrainer=repository.verifyRegisterTrainer
    val resendTrainer=repository.resendTrainer
    val updateTrainerProfile=repository.updateProfile
    //registration
    private val _registrationInputsError = MutableLiveData<Map<String, String>>()
    val registrationInputsError: LiveData<Map<String, String>> get() = _registrationInputsError


    //update profile
    private val _updateProfileInputsError = MutableLiveData<Map<String, String>>()
    val updateProfileInputsError: LiveData<Map<String, String>> get() = _updateProfileInputsError

    fun postRegistration(context:Context,
        phoneNumber: String,
        email: String,
        specializations: String,
        fullName: String,
        addImagesList: ArrayList<Uri>,
        personalImage: Uri?
    ) {
        viewModelScope.launch {
            Log.d("postRegistration", "-- Phone Number: $phoneNumber Email: $email Specializations: $specializations Full Name: $fullName")

            repository.postRegistration(context,phoneNumber,email,fullName, specializations.toString(),addImagesList,personalImage)
        }
    }

    fun updateProfile(
        context: Context,
        address: String,
        about: String,
        price: String,
        slots: String,
        specialization: String,
        fullName: String,
        addImagesList: ArrayList<Uri>,
        personalImage: Uri?,
        selectedDaysList: ArrayList<String>,

        ) {
        viewModelScope.launch {
            Log.d(
                "updateProfileData", """
        -- Full Name: $fullName
        -- Address: $address
        -- About: $about
        -- Price: $price
        -- Slots: $slots
        -- Specialization: $specialization
        -- Selected Days: $selectedDaysList
        -- Add Images URIs: $addImagesList
        -- Personal Image URI: $personalImage
    """.trimIndent()
            )


            repository.updateTrainerProfile(
                context = context,
                address = address,
                about = about,
                pricing = price,
                slots = slots,
                specialization = specialization,
                fullName = fullName,
                addImagesList = addImagesList,
                personalImage = personalImage,
                selectedDaysList = selectedDaysList
            )
        }
    }

    fun loginTrainer(number: String) {
        viewModelScope.launch {
            repository.loginTrainer(number)
        }
    }
    fun verifyRegsiterTrainer(pin: String, mobileNumber: String) {
        viewModelScope.launch {
            repository.verifyRegsiterTrainer(pin,mobileNumber)
        }
    }

    fun verifyTrainer(pin: String, mobileNumber: String) {
        viewModelScope.launch {
            repository.verifyTrainer(pin,mobileNumber)
        }
    }
    fun resendTrainer(mobileNumber: String) {
        viewModelScope.launch {
            repository.resendTrainer(mobileNumber)
        }
    }



    fun validInputs(
        phoneNumber: String,
        email: String,
        fullName: String,
        addImagesList: ArrayList<Uri>
    ) {


        val errors = mutableMapOf<String, String>()

        // Validate price per hour
        if (phoneNumber.isEmpty()) errors["phoneNumber"] = "PhoneNumber is required"

        // Validate venue rules
        if (email.isEmpty()) errors["email"] = "Email is  required"



        // Validate facilities
        if (fullName.isEmpty()) errors["fullName"] = "fullName is required"

        // Validate facilities
        if (addImagesList.size==0) errors["addImagesList"] = "AddProfile image"

        // Update LiveData with errors
        _registrationInputsError.value = errors


    }

    fun validUpdateInputs(
        fullName: String,
        specialization: String,
        address: String,
        about: String,
        price: String,
        slots: String,
        addImagesList: ArrayList<Uri>
    ) {


        val errors = mutableMapOf<String, String>()

        // Validate price per hour
        if (specialization.isEmpty()) errors["specialization"] = "specialization is required"

        // Validate venue rules
        if (address.isEmpty()) errors["address"] = "address is  required"
        if (about.isEmpty()) errors["about"] = "about is required"
        if (price.isEmpty()) errors["price"] = "price is required"
        if (slots.isEmpty()) errors["slots"] = "slots is required"


        // Validate facilities
        if (fullName.isEmpty()) errors["fullName"] = "fullName is required"

        // Validate facilities
        if (addImagesList.size==0) errors["addImagesList"] = "AddProfile image"

        // Update LiveData with errors
        _updateProfileInputsError.value = errors


    }
}