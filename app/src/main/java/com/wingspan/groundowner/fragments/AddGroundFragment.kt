package com.wingspan.groundowner.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentAddGroundBinding
import com.wingspan.groundowner.databinding.FragmentGroundsFragemntBinding
import com.wingspan.groundowner.model.Area
import com.wingspan.groundowner.model.AreaResponse
import com.wingspan.groundowner.model.Cities
import com.wingspan.groundowner.model.CityResponse
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.viewmodel.GroundsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.text.isNullOrEmpty

@AndroidEntryPoint
class AddGroundFragment : Fragment() {

    private var _binding: FragmentAddGroundBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GroundsViewModel by viewModels()
    var citiesList:ArrayList<Cities>? = ArrayList()
    val citiesSpinnerList = ArrayList<String>()
    var areasList:ArrayList<Area>? = ArrayList()
    val areasSpinnerList = ArrayList<String>()
    var cityId:String=""
    var areaId:String=""
    var mapsLink:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentAddGroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setObservers()
        checkLocationPermissionsForLocation()


        //back press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
            OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
            } })

        binding.citySpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // val selectedCityName = parent?.getItemAtPosition(position).toString()
                val selectedCity = citiesList?.get(position)
                cityId = selectedCity?.id!!
                makeAreaApiCall(cityId)
                Log.d("city id","city id${cityId}")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.areaSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // val selectedCityName = parent?.getItemAtPosition(position).toString()
                val selectedArea = areasList?.get(position)
                areaId = selectedArea!!.id.toString()

                Log.d("area id","area id ---${areaId}")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setObservers() {
        viewModel.cityStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleCitySuccess(resource.data)
                is Resource.Error -> handleError(resource.message)
                is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
            }
        }
        viewModel.areaStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleGetAreaSuccess(resource.data)
                is Resource.Error -> handleError(resource.message)
                is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
            }
        }

        viewModel.inputsError.observe(viewLifecycleOwner, Observer { errors ->
            // Display errors for specific fields
            binding?.apply {
                // Ground heading error
                if (errors["groundheadinget"].isNullOrEmpty()) {
                    groundheadingErrorTV.gone()  // Hide if no error
                } else {
                    groundheadingErrorTV.visible()  // Show if error exists
                    groundheadingErrorTV.text = errors["groundheadinget"]
                }

                // Sport type error
                if (errors["sportstypeet"].isNullOrEmpty()) {
                    sportstypeErrorTV.gone()  // Hide if no error
                } else {
                    sportstypeErrorTV.visible()  // Show if error exists
                    sportstypeErrorTV.text = errors["sportstypeet"]
                }

                // Ground address error
                if (errors["groundaddresset"].isNullOrEmpty()) {
                    groundaddressetErrorTV.gone()  // Hide if no error
                } else {
                    groundaddressetErrorTV.visible()  // Show if error exists
                    groundaddressetErrorTV.text = errors["groundaddresset"]
                }

                // Map link error
                if (errors["maplinket"].isNullOrEmpty()) {
                    maplinkErrorTV.gone()  // Hide if no error
                } else {
                    maplinkErrorTV.visible()  // Show if error exists
                    maplinkErrorTV.text = errors["maplinket"]
                }

                // Pincode error
                if (errors["pincodeet"].isNullOrEmpty()) {
                    pincodeErrorTV.gone()  // Hide if no error
                } else {
                    pincodeErrorTV.visible()  // Show if error exists
                    pincodeErrorTV.text = errors["pincodeet"]
                }


            }
            // Scroll to the first error field
            val firstErrorKey = errors.entries.firstOrNull { !it.value.isNullOrEmpty() }?.key
            firstErrorKey?.let { key ->
                val targetView = when (key) {
                    "groundheadinget" -> binding.groundheadinget
                    "sportstypeet" -> binding.sportstypeet
                    "groundaddresset" -> binding.groundaddresset
                    "pincodeet" -> binding.pincodeet

                    else -> null
                }
                targetView?.requestFocus()
                targetView?.let {
                    // Optional: scroll if inside ScrollView or NestedScrollView
                    (binding.root as? ScrollView)?.smoothScrollTo(0, it.top)
                }
            }

            // Check if all required fields are filled
            val isFormValid = errors["groundheadinget"].isNullOrEmpty() &&
                    errors["sportstypeet"].isNullOrEmpty() &&
                    errors["groundaddresset"].isNullOrEmpty() &&
                    errors["maplinket"].isNullOrEmpty() &&
                    errors["pincodeet"].isNullOrEmpty() &&
                    errors["facilitieset"].isNullOrEmpty() &&
                    errors["venueet"].isNullOrEmpty() &&
                    errors["venuerules"].isNullOrEmpty() &&
                    errors["priceperhour"].isNullOrEmpty()

            // Check if there are no errors and all fields are filled
            val allFieldsFilled = binding.groundheadinget?.text!!.isNotEmpty() &&
                    binding.sportstypeet.text!!.isNotEmpty() &&
                    binding.groundaddresset.text!!.isNotEmpty() &&

                    binding.pincodeet?.text!!.isNotEmpty()


            val isFormComplete = allFieldsFilled && isFormValid
            Log.d("status check", "status check--->${errors.values}")
            // If the form is valid, call the ViewModel function to make the API call
            if (isFormComplete) {
                // You can call the API method from ViewModel here
                Log.d("status check", "status check--->${isFormComplete}")
               //Api call here

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.addgroundFragment, true) // <-- This will remove AddGroundFragment
                    .build()
                findNavController().navigate(R.id.action_addgroundFragment_to_addmorefragment, null, navOptions)
            }


        })

    }
    private fun handleCitySuccess(data: CityResponse?) {
        Log.e(" handleCitySuccess", "---> $data")
        citiesSpinnerList.clear()

        citiesList?.let { list ->
            list.clear()
            data?.cities?.let { cities ->
                cities.forEach { city ->
                    citiesSpinnerList.add(city.name ?: "")
                }
                list.addAll(cities)
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, citiesSpinnerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.citySpinner?.adapter = adapter
    }
    private fun handleError(message: String?) {
        Log.e(" failed", "---> $message")
        Singleton.showToast(requireContext(), message ?: "Error occurred")
    }
    private fun handleGetAreaSuccess(data: AreaResponse?) {
        Log.d("get handleGetGroundSuccess","response--->${data}")
        areasSpinnerList.clear()

        areasList?.let { list ->
            list.clear()
            data?.areas?.let { areas ->
                areas.forEach { area ->
                    areasSpinnerList.add(area.name ?: "")
                }
                list.addAll(areas)
            }
        }
        setAreaSpinners()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkLocationPermissionsForLocation() {
        val isGooglePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        if (isGooglePlayServicesAvailable != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), isGooglePlayServicesAvailable, 1001)?.show()
            return
        }

        // Request permissions if not granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }


    }
    val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Permissions granted, proceed with location
            // getUserLocation()
        } else {
            // Show rationale or direct user to settings
            Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setAreaSpinners() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, areasSpinnerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.areaSpinner?.adapter = adapter
    }
    private fun setUI() {
        binding.apply {
            backIcon.setDebouncedClickListener {
                findNavController().popBackStack()
            }
            maplinkiv.setDebouncedClickListener(){

                if (isLocationEnabled()) {
                    loadingProgressBar.visibility=View.VISIBLE
                    getCurrentLocation()
                } else {
                    Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_SHORT).show()
                    Log.e("Location", "Location services are OFF")
                }
            }
            btnsaveground.setDebouncedClickListener {

                viewModel.validInputs1(
                    pincodeet.text.toString(),areaId,cityId,groundaddresset.text.toString(),sportstypeet.text.toString(),
                    groundheadinget.text.toString())

            }
        }
    }
    private fun makeAreaApiCall(cityId: String) {
        if (Singleton.isNetworkAvailable(requireContext())) {

            viewModel.getAreas(cityId)
        } else {
            Singleton.isNetworkAvailable(requireContext())
        }
    }
    fun View.visible() {
        this.visibility = View.VISIBLE
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(LocationManager::class.java)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun getCurrentLocation() {

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {

                    if(Singleton.isNetworkAvailable(requireContext())){
                        binding.loadingProgressBar.visibility=View.GONE
                        mapsLink=getMapLinkFromLocation(location.latitude, location.longitude)
                        binding.maplinket.setText(mapsLink)
                        Log.d("Location", "Lat: ${location.latitude}, Lng: ${location.longitude}...$mapsLink")
                    }else{
                        Singleton.showNetworkAlertDialog(requireContext())
                    }


                } else {
                    Log.e("Location", "Location is null")
                }
            }.addOnFailureListener {

                Log.e("Location", "Error fetching location: ${it.message}")
            }
        } else {
            Log.e("Location", "Permissions not granted")
        }


    }

    private fun getMapLinkFromLocation(latitude: Double, longitude: Double):String {
        return "https://www.google.com/maps?q=$latitude,$longitude"
    }
    // Extension function to make the View gone
    fun View.gone() {
        this.visibility = View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}