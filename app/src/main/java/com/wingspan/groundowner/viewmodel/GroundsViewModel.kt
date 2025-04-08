package com.wingspan.groundowner.viewmodel

import AreaResponse
import CityResponse
import DeleteResponse
import GetGroundsResponse
import PostGroundsResponse
import Slot
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.groundowner.repository.GroundRepository
import com.wingspan.groundowner.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GroundsViewModel @Inject constructor(private val repository: GroundRepository): ViewModel() {
    // city status
    private val _cityStatus = MutableLiveData<Resource<CityResponse>>()
    val cityStatus: LiveData<Resource<CityResponse>> get() = _cityStatus

    // areas status
    private val _areaStatus = MutableLiveData<Resource<AreaResponse>>()
    val areaStatus: LiveData<Resource<AreaResponse>> get() = _areaStatus

//get grounds
    private val _getGroundStatus = MutableLiveData<Resource<GetGroundsResponse>>()
    val getGroundStatus: LiveData<Resource<GetGroundsResponse>> get() = _getGroundStatus

    //post ground
    private val _postGroundStatus = MutableLiveData<Resource<PostGroundsResponse>>()
    val postGroundStatus: LiveData<Resource<PostGroundsResponse>> get() = _postGroundStatus


//delete ground
    private val _deleteGroundStatus = MutableLiveData<Resource<DeleteResponse>>()
    val deleteGroundStatus: LiveData<Resource<DeleteResponse>> get() = _deleteGroundStatus

    private val _inputsError = MutableLiveData<Map<String, String>>()
    val inputsError: LiveData<Map<String, String>> get() = _inputsError

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> get() = _isDataValid

    fun getCities() {
        _cityStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.getCity()
            _cityStatus.postValue(response)
        }
    }
    fun getAreas(id:String) {
        _areaStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.getArea(id)
            _areaStatus.postValue(response)
        }
    }


    fun getGroundData() {
        _getGroundStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.getGroundData()
            Log.d("getGroundData ", "ground view model-- ${response}")
            _getGroundStatus.postValue(response)
        }
    }

    fun deleteGround(id:Int) {
        _deleteGroundStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.deleteGround(id)
            Log.d("deleteGround ", "deleteGround view model-- ${response}")
            _deleteGroundStatus.postValue(response)
        }
    }

    fun validInputs(
        priceperhour: String, venuerules: String, venueet: String, facilitieset: String, pincodeet: String,
        areaId: String, cityId: String, maplinket: String, groundaddresset: String, sportstypeet: String, groundheadinget: String
    ) {
        val errors = mutableMapOf<String, String>()

        // Validate price per hour
        if (priceperhour.isEmpty()) errors["priceperhour"] = "Price per hour is required"

        // Validate venue rules
        if (venuerules.isEmpty()) errors["venuerules"] = "Venue rules are required"

        // Validate venue
        if (venueet.isEmpty()) errors["venueet"] = "Venue is required"

        // Validate facilities
        if (facilitieset.isEmpty()) errors["facilitieset"] = "Facilities are required"

        // Validate pincode
        if (pincodeet.isEmpty()) errors["pincodeet"] = "Pincode is required"
        else if (!isValidPincode(pincodeet)) errors["pincodeet"] = "Invalid pincode"



        // Validate map link
        if (maplinket.isEmpty()) errors["maplinket"] = "Map link is required"

        // Validate ground address
        if (groundaddresset.isEmpty()) errors["groundaddresset"] = "Ground address is required"

        // Validate sport type
        if (sportstypeet.isEmpty()) errors["sportstypeet"] = "Sport type is required"

        // Validate ground heading
        if (groundheadinget.isEmpty()) errors["groundheadinget"] = "Ground heading is required"

        // Update LiveData with errors
        _inputsError.value = errors

        // Validate overall input
        _isDataValid.value = errors.isEmpty()
    }



    // Example validation for pincode (you can customize it based on your requirement)
    private fun isValidPincode(pincode: String): Boolean {
        return pincode.length == 6 && pincode.all { it.isDigit() }
    }


    fun postGroundData(
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
    )
    {
        _postGroundStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.postGroundData(priceperhour,venuerules,venueet,facilitieset,pincodeet,areaId,
                cityId,maplinket,groundaddresset,sportstypeet,groundheadinget,imageNamesList,addImagesList,selectedTimeSlots)
            Log.d("getGroundData Error", "ground view model ${response}")
            _postGroundStatus.postValue(response)
        }

    }
}