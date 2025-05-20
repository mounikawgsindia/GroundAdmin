package com.wingspan.groundowner.viewmodel

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

    val postRegistration=repository.registration

    fun postRegistration(
        phoneNumber: String,
        email: String,
        specializations:String,
        fullName: String,
        addImagesList: ArrayList<Uri>
    ) {
        viewModelScope.launch {
            Log.d("postRegistration", "-- Phone Number: $phoneNumber Email: $email Specializations: $specializations Full Name: $fullName")

            repository.postRegistration(phoneNumber,email,fullName, specializations.toString(),addImagesList)
        }
    }
//registration
    private val _registrationInputsError = MutableLiveData<Map<String, String>>()
    val registrationInputsError: LiveData<Map<String, String>> get() = _registrationInputsError


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
}