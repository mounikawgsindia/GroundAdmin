package com.wingspan.groundowner.viewmodel

import com.wingspan.groundowner.model.LoginResponse
import com.wingspan.groundowner.model.ResponseData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope



import com.wingspan.groundowner.repository.AuthRepository
import com.wingspan.groundowner.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {


    // Login status
    private val _loginStatus = MutableLiveData<Resource<ResponseData>>()
    val loginStatus: LiveData<Resource<ResponseData>> get() = _loginStatus


    // Registration status
    private val _registrationStatus = MutableLiveData<Resource<ResponseData>>()
    val registrationStatus: LiveData<Resource<ResponseData>> get() = _registrationStatus

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> get() = _isDataValid

    private val _mobileNumberError = MutableLiveData<String>()
    val mobileNumberError: LiveData<String> get() = _mobileNumberError

    private val _mobilenumberPinError = MutableLiveData<String>()
    val mobilenumberPinError: LiveData<String> get() = _mobilenumberPinError

    // Verify status
    private val _mobilenumberverifyStatus = MutableLiveData<Resource<LoginResponse>>()
    val mobilenumberverifyStatus: LiveData<Resource<LoginResponse>> get() = _mobilenumberverifyStatus

    // Registration validation
    private val _usernameError = MutableLiveData<String>()
    val usernameError: LiveData<String> get() = _usernameError

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String> get() = _emailError

    // Resend status
    private val _resendStatus = MutableLiveData<Resource<ResponseData>>()
    val resendStatus: LiveData<Resource<ResponseData>> get() = _resendStatus

    fun login(mobileNumber: String) {
        _loginStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.login(mobileNumber)
            _loginStatus.postValue(response)
        }
    }

    fun registration(mobileNumber: String,email:String?,username:String?) {
        _registrationStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.register(username!!,email!!,mobileNumber)
            _registrationStatus.postValue(response)

        }
    }

    fun pinValidation(pin: String?, mobileNumber: String?) {
        _loginStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.verifyMobileNumber(pin!!, mobileNumber!!)
            _mobilenumberverifyStatus.postValue(response)
        }
    }

    fun resend(mobileNumber: String?) {
        _resendStatus.value = Resource.Loading() // Show loading state
        viewModelScope.launch {
            val response = repository.resend(mobileNumber!!)
            _resendStatus.postValue(response)
        }
    }

    fun isValidInputs(mobileNumber: String) {
        _mobileNumberError.value = when {
            mobileNumber.isEmpty() -> "Mobile number is required"
            !isValidNumber(mobileNumber) -> "Mobile number must be 10 characters"
            else -> null
        }
        _isDataValid.value = _mobileNumberError.value == null
    }

    fun isPinValidation(pin: String) {
        _mobilenumberPinError.value = when {
            pin.isEmpty() -> "Pin is required"
            !isValidPin(pin) -> "Pin must be 6 characters"
            else -> null
        }
        _isDataValid.value = _mobilenumberPinError.value == null
    }

    fun validRegistration(email: String?, mobileNumber: String?, username: String?) {
        _mobileNumberError.value = when {
            mobileNumber!!.isEmpty() -> "Mobile number is required"
            !isValidNumber(mobileNumber) -> "Mobile number must be 10 characters"
            else -> null
        }
        _emailError.value = when {
            email!!.isEmpty() -> "Email is required"
            !isValidEmail(email) -> "Invalid email"
            else -> null
        }
        _usernameError.value = if (username!!.isEmpty()) "Username is required" else null

        _isDataValid.value = _mobileNumberError.value == null && _emailError.value == null && _usernameError.value == null
    }

    private fun isValidNumber(number: String): Boolean {
        return number.length == 10
    }

    private fun isValidPin(pin: String): Boolean {
        return pin.length == 6
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}


