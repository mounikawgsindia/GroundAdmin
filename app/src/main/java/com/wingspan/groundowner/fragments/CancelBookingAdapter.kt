package com.wingspan.groundowner.fragments

import Booking
import CanceledBooking
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingspan.groundowner.adapters.BookingAdapter
import com.wingspan.groundowner.databinding.ItemBookingBinding
import com.wingspan.groundowner.databinding.OrderBottomSheetLayoutBinding
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener

class CancelBookingAdapter(var context:Context,private val bookings: List<CanceledBooking>) :
    RecyclerView.Adapter<CancelBookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }



    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.binding.apply {
           // username.text = booking.user.username.toString()
            price.text=booking.totalAmount.toString()
            sports.text=booking.sport
            slotList.text = booking.slots
            bookingDate.text = booking.date

            arrowIcon.setDebouncedClickListener {
                bottomSheetDialog(booking)
            }
        }
    }
    private fun bottomSheetDialog(bookings: CanceledBooking) {

        val dialog = BottomSheetDialog(context)
        val binding = OrderBottomSheetLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.timetv.text=bookings.date
        // binding.groundnametv.text=bookings.groundId.toString()
        binding.slotstv.text=bookings.slots.toString()
        binding.sportstypetv.text=bookings.sport
        binding.pricetv.text=bookings.totalAmount.toString()
//        binding.emailtv.text=bookings.user.email
//        binding.numbertv.text=bookings.user.phoneNumber
        binding.statutv.text=bookings.status
        binding.apply {
//            if(!bookings.payments.isEmpty())
//            {
//                paymenttypetv.text= orders.payments.firstOrNull()?.paymentType.toString()
//                statustv.text=orders.payments.firstOrNull()?.status.toString()
//                createdtv.text=orders.payments.firstOrNull()?.createdAt.toString()
//            }else{
//                paymentImg.visibility=View.GONE
//                linearLayoutHorizontal.visibility=View.GONE
//                statusll.visibility=View.GONE
//                createdll.visibility=View.GONE
//            }

        }

        dialog.show()
    }

    override fun getItemCount(): Int{
        return  bookings.size
    }
}