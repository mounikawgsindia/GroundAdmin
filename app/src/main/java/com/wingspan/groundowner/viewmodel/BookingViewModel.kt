package com.wingspan.groundowner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wingspan.groundowner.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(var repository : BookingRepository) : ViewModel() {

    val getbooking=repository.getbookings

    fun grounBookings(){
        viewModelScope.launch {
            repository.groundBooking()
        }
    }

}