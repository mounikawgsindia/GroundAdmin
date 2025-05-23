package com.wingspan.groundowner.fragments

import com.wingspan.groundowner.model.Booking
import com.wingspan.groundowner.model.CanceledBooking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator

import com.wingspan.groundowner.adapters.BookingPagerAdapter
import com.wingspan.groundowner.databinding.FragmentBokkingsBinding
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton


import com.wingspan.groundowner.utils.UserPreferences
import com.wingspan.groundowner.viewmodel.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class BookingsFragment : Fragment() {
    private var _binding: FragmentBokkingsBinding? = null
    private val binding get() = _binding!!
    lateinit var alertDialog:AlertDialog
     var bookingList=ArrayList<Booking>()
    var bookingCancelList=ArrayList<CanceledBooking>()
    val viewmodel: BookingViewModel by viewModels()
    lateinit var adapter: BookingPagerAdapter
    @Inject
    lateinit var sharedPreferences:UserPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentBokkingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //display tabview
        viewPagerSetUp()
        makeApiCall()
        //setUI()
        setObservers()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
            OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

            }

        })
    }

    private fun viewPagerSetUp() {

        Log.e("list count ","--->${bookingList.size}")
        val titles= listOf("Confirm","Canceled")
         adapter = BookingPagerAdapter(this,bookingList,bookingCancelList)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }



    private fun makeApiCall() {
       if(Singleton.isNetworkAvailable(requireActivity())){
           viewmodel.grounBookings()
       }else{
           Singleton.showNetworkAlertDialog(requireContext())
       }
    }

    private fun setObservers() {
        binding.apply {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewmodel.getbooking.collect { state ->
                        when (state) {
                            is Resource.Loading -> { shimmerStart()}
                            is Resource.Success -> {
                                shimmerStop()
                                bookingList.clear()
                                val bookings = state.data?.data?.activeBookings
                                val cancelBookings = state.data?.data?.canceledBookings
                                binding.apply {
                                    tvActiveCount.text= state.data?.data?.totalActive.toString()
                                    tvCancelledCount.text=state.data?.data?.totalCanceled.toString()
                                }
                                if (!bookings.isNullOrEmpty()) {
                                    bookingList.addAll(bookings)
                                    cancelBookings?.let {
                                        bookingCancelList.addAll(it)
                                    }
                                    viewPagerSetUp()
                                 //   setUI()
                                    updateUIVisibility()
                                }
                            }
                            is Resource.Error -> {
                                shimmerStop()
                                Log.e("getbooking ","--->${state.message.toString()}")
                                Singleton.showToast(requireContext(), state.message.toString())
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    fun shimmerStop(){
        binding.shimmerLayout.visibility=View.GONE
        binding.shimmerLayout.stopShimmer()
        binding.viewPager.visibility = View.VISIBLE
    }
    fun shimmerStart(){
        binding.shimmerLayout.visibility=View.VISIBLE
        binding.shimmerLayout.startShimmer()
        binding.viewPager.visibility = View.GONE
    }


    private fun updateUIVisibility() {
        if (bookingList.isEmpty()) {
            binding.viewPager.visibility = View.GONE
          //  binding.noRecordsLayout.visibility = View.VISIBLE
        } else {
            binding.viewPager.visibility = View.VISIBLE
           // binding.noRecordsLayout.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}