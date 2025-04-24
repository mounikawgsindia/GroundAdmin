package com.wingspan.groundowner.fragments

import BookingModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator

import com.wingspan.groundowner.adapters.BookingPagerAdapter
import com.wingspan.groundowner.databinding.FragmentBokkingsBinding


import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookingsFragment : Fragment() {
    private var _binding: FragmentBokkingsBinding? = null
    private val binding get() = _binding!!
    lateinit var alertDialog:AlertDialog
     var bookingList=ArrayList<BookingModel>()

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



        //makeApiCall()
        viewPagerSetUp()


        setUI()
        setObservers()
    }

    private fun viewPagerSetUp() {
        bookingList.addAll(getDummyBookings())

        val titles= listOf("Conform","Pending","Canceled")
        val adapter = BookingPagerAdapter(this,bookingList)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    private fun getDummyBookings(): List<BookingModel> {
        return listOf(
            BookingModel("Rahul", "Ground A", "B001", "10:00 AM - 11:00 AM", "₹500", "Cricket", "confirmed", "2025-04-23", "2025-04-20"),
            BookingModel("Amit", "Ground B", "B002", "11:00 AM - 12:00 PM", "₹600", "Football", "pending", "2025-04-25", "2025-04-21"),
            BookingModel("Sneha", "Ground C", "B003", "02:00 PM - 03:00 PM", "₹450", "Badminton", "cancelled", "2025-04-26", "2025-04-22"),
            BookingModel("Priya", "Ground D", "B004", "04:00 PM - 05:00 PM", "₹700", "Tennis", "confirmed", "2025-04-27", "2025-04-22")
        )
    }

    private fun makeApiCall() {
//       if(Singleton.isNetworkAvailable(requireActivity())){
//
//       }else{
//
//       }
    }

    private fun setObservers() {
        binding.apply {

        }
    }



    private fun setUI() {
        binding.apply {
            tvActiveCount.text= bookingList.count { it.status.equals("confirmed", ignoreCase = true) } .toString()
            tvCancelledCount.text=bookingList.count { it.status.equals("cancelled", ignoreCase = true) }.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}