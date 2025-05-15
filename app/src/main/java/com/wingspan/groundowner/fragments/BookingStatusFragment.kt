package com.wingspan.groundowner.fragments

import com.wingspan.groundowner.model.Booking
import com.wingspan.groundowner.model.CanceledBooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingspan.groundowner.adapters.BookingAdapter
import com.wingspan.groundowner.databinding.FragmentBookingStatusBinding


class BookingStatusFragment : Fragment() {

    private var _binding: FragmentBookingStatusBinding? = null
    private val binding get() = _binding!!

    private var bookingList: ArrayList<Booking>? = null
    private var cancelList: ArrayList<CanceledBooking>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingList = arguments?.getParcelableArrayList("bookingList")
        cancelList = arguments?.getParcelableArrayList("cancelList")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        when {
            bookingList != null -> {
                binding.recyclerView.adapter = BookingAdapter(requireContext(), bookingList!!)
                toggleView(bookingList!!.isEmpty())
            }
            cancelList != null -> {
                binding.recyclerView.adapter = CancelBookingAdapter(requireContext(), cancelList!!)
                toggleView(cancelList!!.isEmpty())
            }
            else -> {
                toggleView(true)
            }
        }
    }

    private fun toggleView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.noRecordsLayout.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.noRecordsLayout.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(bookings: ArrayList<Booking>): BookingStatusFragment {
            val fragment = BookingStatusFragment()
            val args = Bundle()
            args.putParcelableArrayList("bookingList", bookings)
            fragment.arguments = args
            return fragment
        }

        fun newCancelledInstance(cancelled: ArrayList<CanceledBooking>): BookingStatusFragment {
            val fragment = BookingStatusFragment()
            val args = Bundle()
            args.putParcelableArrayList("cancelList", cancelled)
            fragment.arguments = args
            return fragment
        }
    }

}