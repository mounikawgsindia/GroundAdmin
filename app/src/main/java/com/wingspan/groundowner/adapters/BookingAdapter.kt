package com.wingspan.groundowner.adapters

import BookingModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wingspan.groundowner.databinding.ItemBookingBinding

class BookingAdapter(private val bookings: List<BookingModel>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }



    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.binding.apply {
            tvUser.text = booking.user
//            tvGround.text = booking.ground
//            tvAmount.text = booking.amount
//            tvSlot.text = booking.slots
            tvDate.text = booking.date
            tvStatus.text = booking.status
        }
    }

    override fun getItemCount(): Int = bookings.size
}