package com.wingspan.groundowner.fragments

import com.wingspan.groundowner.model.GetGround
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wingspan.groundowner.R
import com.wingspan.groundowner.adapters.IntroSlidesAdapter
import com.wingspan.groundowner.databinding.FragmentDisplayGroundBinding

import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener


class DisplayGroundFragment : Fragment() {


    private var _binding: FragmentDisplayGroundBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: IntroSlidesAdapter
    lateinit var navController: NavController
    //var imagesList = listOf("https://wingspan-s3bucket.s3.amazonaws.com/uploads/1743917859758_ground1.jpg","https://wingspan-s3bucket.s3.eu-north-1.amazonaws.com/uploads/1743918199901_ground2.jpeg","https://wingspan-s3bucket.s3.eu-north-1.amazonaws.com/uploads/1743918067774_ground1.jpg")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentDisplayGroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setUI()


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
            OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

            }

        })
    }

    private fun setUI() {
        binding.apply {
            val getGround = arguments?.getSerializable("ground_key") as? GetGround

            getGround?.let {
                // Use the data
                Log.d("DisplayFragment", "Ground heading: ${it.groundHeading}")

                    tvAboutVenue.text = it.aboutVenue
                    sportstype.text = it.sportsType
                    amentieslisttv.text = it.facilities
                    venuerulestv.text =it.venueRules
                    citynametv.text = it.cityName.toString()

                    areatv.text = it.areaName.toString()
                    pincodetv.text = it.pincode
                    addresstv.text = it.groundAddress
                    locationtv.text = it.groundLocationLink
                    pricetv.text = it.pricePerHour.toString()
                    slotstv.text=it.slots?.joinToString(separator = " , ")?:""


                //adpter
                adapter = IntroSlidesAdapter(requireContext(),getGround.imageUrl , navController, getGround)
                imageViewPager.adapter = adapter
                circleIndicator1.setViewPager(imageViewPager)

            }


            backIcon.setDebouncedClickListener(){
                navigateBack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        // Show BottomNavigationView again when coming back to the bottom navigation fragments
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        // Hide BottomNavigationView when navigating away
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }
    private fun navigateBack() {
        findNavController().popBackStack(R.id.groundsFragemnt, false)
    }
}