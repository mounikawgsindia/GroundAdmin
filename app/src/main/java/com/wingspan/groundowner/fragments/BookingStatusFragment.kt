package com.wingspan.groundowner.fragments

import BookingModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingspan.groundowner.R
import com.wingspan.groundowner.adapters.BookingAdapter
import com.wingspan.groundowner.databinding.FragmentBookingStatusBinding


class BookingStatusFragment : Fragment() {

    private var _binding: FragmentBookingStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookingList: ArrayList<BookingModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookingList = arguments?.getParcelableArrayList("bookings") ?: arrayListOf()
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
        binding.recyclerView.adapter = BookingAdapter(bookingList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(bookings: ArrayList<BookingModel>) = BookingStatusFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("bookings", bookings)
            }
        }
    }
}