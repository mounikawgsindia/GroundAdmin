package com.wingspan.groundowner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.wingspan.groundowner.model.LoginResponse

import com.wingspan.groundowner.repository.AuthRepository
import com.wingspan.groundowner.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<LoginResponse>>()
    val loginStatus: LiveData<Resource<LoginResponse>> get() = _loginStatus

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> get() = _isDataValid

    private val _mobileNumberError = MutableLiveData<String>()
    val mobileNumberError: LiveData<String> get() = _mobileNumberError


    private val _mobilenumberPinError = MutableLiveData<String>()
    val mobilenumberPinError: LiveData<String> get() = _mobilenumberPinError

    private val _modeleValidationNumberError = MutableLiveData<String>()
    val modeleValidationNumberError: LiveData<String> get() = _modeleValidationNumberError


    fun login(mobileNumber: String) {
        _loginStatus.value = Resource.Loading()  // Show loading state

        viewModelScope.launch {
            try {
                val response = repository.login(mobileNumber)
                _loginStatus.value = response
            } catch (e: Exception) {
                _loginStatus.value = Resource.Error("Login failed: ${e.message}")
            }
        }
    }

    fun pinValidation(pin: String) {
        _loginStatus.value = Resource.Loading()  // Show loading state

        viewModelScope.launch {
            try {
                val response = repository.login(pin)
                _loginStatus.value = response
            } catch (e: Exception) {
                _loginStatus.value = Resource.Error("Login failed: ${e.message}")
            }
        }
    }



    fun isValidInputs(mobileNumber: String) {

        _mobileNumberError.value =
            if (mobileNumber.isEmpty()) "Mobilenumber is required" else if (!isValidNumber (mobileNumber)) "MobileNumber must be 10 characters" else null
        _isDataValid.value = _mobileNumberError.value == null

    }

    fun isPinValidation(pin: String) {

        _mobileNumberError.value =
            if (pin.isEmpty()) "pin is required" else if (!isValidPin (pin)) "pin must be 6 characters" else null
        _isDataValid.value = _mobilenumberPinError.value == null

    }
    private fun isValidNumber (number:String):Boolean{
        return number.length==10
    }
    private fun isValidPin (number:String):Boolean{
        return number.length==6
    }
}