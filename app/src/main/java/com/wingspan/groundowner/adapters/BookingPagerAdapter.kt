package com.wingspan.groundowner.adapters

import com.wingspan.groundowner.model.Booking
import com.wingspan.groundowner.model.CanceledBooking
import android.util.Log

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wingspan.groundowner.fragments.BookingStatusFragment

class BookingPagerAdapter(
    fragment: Fragment,
    val bookingList: ArrayList<Booking>,val
    bookingCancelList: ArrayList<CanceledBooking>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                // Confirmed bookings

                Log.d("Adapter", "Confirmed bookings: ${bookingList.size}")
                BookingStatusFragment.newInstance(ArrayList(bookingList))
            }

            1 -> {
                // Cancelled bookings
                Log.d("Adapter", "Cancelled bookings: ${bookingCancelList.size}")
                BookingStatusFragment.newCancelledInstance(ArrayList(bookingCancelList))
            }

            else -> {
                // Pending bookings
                val filteredList =
                    bookingList.filter { it.status.equals("pending", ignoreCase = true) }
                Log.d("Adapter", "Pending bookings: ${filteredList.size}")
                BookingStatusFragment.newInstance(ArrayList(filteredList))
            }
        }

    }

}
