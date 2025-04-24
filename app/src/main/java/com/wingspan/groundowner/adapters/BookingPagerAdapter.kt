package com.wingspan.groundowner.adapters

import BookingModel
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wingspan.groundowner.fragments.BookingStatusFragment

class BookingPagerAdapter(fragment: Fragment,val  bookingList: ArrayList<BookingModel>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val status = when (position) {
            0 -> "confirmed"
            1 -> "pending"
            else -> "cancelled"
        }

        val filteredList = bookingList.filter { it.status.equals(status, ignoreCase = true) }
        return BookingStatusFragment.newInstance(ArrayList(filteredList))

    }
}