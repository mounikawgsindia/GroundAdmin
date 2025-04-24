    package com.wingspan.groundowner.fragments

    import Area
    import AreaResponse
    import Cities
    import CityResponse
    import GetGround

    import GetGroundsResponse
    import PostGroundsResponse
    import Slot
    import android.Manifest
    import android.R
    import android.annotation.SuppressLint
    import android.app.Activity
    import android.content.ContentResolver
    import android.content.Intent
    import android.content.SharedPreferences
    import android.content.pm.PackageManager
    import android.graphics.Color
    import android.graphics.drawable.ColorDrawable
    import android.location.LocationManager
    import android.net.Uri
    import android.os.Build
    import android.os.Bundle
    import android.provider.OpenableColumns
    import android.provider.Settings
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import android.widget.Toast
    import androidx.annotation.RequiresApi
    import androidx.appcompat.app.AlertDialog
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.Observer
    import androidx.navigation.NavController
    import androidx.navigation.fragment.findNavController
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.google.android.gms.common.ConnectionResult
    import com.google.android.gms.common.GoogleApiAvailability
    import com.google.android.gms.location.LocationServices
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import com.google.gson.Gson
    import com.wingspan.groundowner.activities.MainActivity
    import com.wingspan.groundowner.adapters.GroundAdapter
    import com.wingspan.groundowner.adapters.ImagesAdapterUpdate
    import com.wingspan.groundowner.databinding.FragmentAddGroundBinding
    import com.wingspan.groundowner.databinding.FragmentGroundsFragemntBinding
    import com.wingspan.groundowner.utils.Resource
    import com.wingspan.groundowner.utils.Singleton
    import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
    import com.wingspan.groundowner.utils.UserPreferences
    import com.wingspan.groundowner.viewmodel.GroundsViewModel


    import dagger.hilt.android.AndroidEntryPoint
    import javax.inject.Inject

    @AndroidEntryPoint
    class GroundsFragemnt : Fragment() {

        companion object{
            private val LOCATION_REQUEST_CODE = 100
            val REQUEST_PERMISSION_CODE = 1001
            private val PICK_IMAGES_REQUEST = 3
        }
        private var _binding: FragmentGroundsFragemntBinding? = null
        private val binding get() = _binding!!
        var alertBinding: FragmentAddGroundBinding? = null
        var cityId:String=""
        var areaId:String=""
        var mapsLink:String=""
        private var addImagesList = ArrayList<Uri>()
        private var imageNamesList=ArrayList<String>()
        private lateinit var addImagesAdapter: ImagesAdapterUpdate
        private lateinit var groundAdapter: GroundAdapter
        var gropundList=ArrayList<GetGround>()
        private val viewModel: GroundsViewModel by viewModels()
        var citiesList:ArrayList<Cities>? = ArrayList()
        val citiesSpinnerList = ArrayList<String>()
        var areasList:ArrayList<Area>? = ArrayList()
        val areasSpinnerList = ArrayList<String>()
        lateinit var dialog:AlertDialog
        val selectedTimeSlots = mutableListOf<Slot>()
        private lateinit var navController: NavController

        @Inject
        lateinit var sharedPreferences: UserPreferences
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            _binding = FragmentGroundsFragemntBinding.inflate(inflater, container, false)
            return binding.root
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            checkAndRequestPermissions()
            checkLocationPermissionsForLocation()
            navController = findNavController()
            setUI()
            setObservers()
            setRecyclew()
            apiCall()

        }

        private fun apiCall() {
            if (Singleton.isNetworkAvailable(requireContext())) {
                binding.noInternetLayout.visibility=View.GONE
                binding.btnAddItem.visibility=View.VISIBLE
                viewModel.getGroundData()
            } else {
               binding.noInternetLayout.visibility=View.VISIBLE
               binding.btnAddItem.visibility=View.GONE

            }
        }

        private fun checkAndRequestPermissions() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission not granted, request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        REQUEST_PERMISSION_CODE
                    )
                }
            } else {
                // For Android versions below 13, use READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission not granted, request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_CODE
                    )
                }
            }
        }

        private fun setRecyclew() {
            binding.groundrv?.apply {
                 groundAdapter= GroundAdapter(requireContext(),gropundList,viewModel,navController)
                 layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
                 adapter=groundAdapter
            }
        }

        private fun setObservers() {
           viewModel.cityStatus.observe(viewLifecycleOwner){ resource ->
               when(resource){
                   is Resource.Success -> handleCitySuccess(resource.data)
                   is Resource.Error -> handleLoginError(resource.message)
                   is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
               }
           }
            viewModel.deleteGroundStatus.observe(viewLifecycleOwner){ resource ->
                when(resource){
                    is Resource.Success -> handleDeleteGroundSuccess(resource.data?.message!!)
                    is Resource.Error -> handleLoginError(resource.message)
                    is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
                }
            }
            viewModel.getGroundStatus.observe(viewLifecycleOwner){ resource ->
                when(resource) {
                    is Resource.Success -> {
                        binding.apply {
                            shimmerLayout.stopShimmer()
                            shimmerLayout.visibility = View.GONE
                            groundrv.visibility = View.VISIBLE
                            handleGetGroundSuccess(resource.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.apply {
                            shimmerLayout.stopShimmer()
                            shimmerLayout.visibility = View.GONE
                            groundrv.visibility = View.VISIBLE
                            if(resource.message.equals("Invalid or expired token"))
                            {
                                sharedPreferences.logoutAdmin()
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                requireActivity().finish()
                            }

                        }

                        handleLoginError(resource.message)}
                    is Resource.Loading -> {
                        Log.d("Loading", "---> Verification in progress...")
                       binding.apply {
                           shimmerLayout.visibility = View.VISIBLE
                           shimmerLayout.startShimmer()
                           groundrv.visibility = View.GONE
                           noInternetLayout.visibility = View.GONE
                       }
                    }
                }
            }
            viewModel.areaStatus.observe(viewLifecycleOwner){ resource ->
                when(resource){
                    is Resource.Success -> handleGetAreaSuccess(resource.data)
                    is Resource.Error -> handleLoginError(resource.message)
                    is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
                }
            }
            viewModel.postGroundStatus.observe(viewLifecycleOwner){ resource ->
                when(resource){
                    is Resource.Success -> handlePostgroundSuccess(resource.data)
                    is Resource.Error -> handleLoginError(resource.message)
                    is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
                }
            }
            viewModel.inputsError.observe(viewLifecycleOwner, Observer { errors ->
                // Display errors for specific fields
                alertBinding?.apply {
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

                    // Facilities error
                    if (errors["facilitieset"].isNullOrEmpty()) {
                        facilitiesErrorTV.gone()  // Hide if no error
                    } else {
                        facilitiesErrorTV.visible()  // Show if error exists
                        facilitiesErrorTV.text = errors["facilitieset"]
                    }

                    // Venue error
                    if (errors["venueet"].isNullOrEmpty()) {
                        venueErrorTV.gone()  // Hide if no error
                    } else {
                        venueErrorTV.visible()  // Show if error exists
                        venueErrorTV.text = errors["venueet"]
                    }

                    // Venue rules error
                    if (errors["venuerules"].isNullOrEmpty()) {
                        venuerulesErrorTV.gone()  // Hide if no error
                    } else {
                        venuerulesErrorTV.visible()  // Show if error exists
                        venuerulesErrorTV.text = errors["venuerules"]
                    }

                    // Price per hour error
                    if (errors["priceperhour"]?.isNullOrEmpty() == true) {
                        priceperhourErrorTV.gone()  // Hide if no error
                    } else {
                        priceperhourErrorTV.visible()  // Show if error exists
                        priceperhourErrorTV.text = errors["priceperhour"]
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
                val allFieldsFilled = alertBinding?.groundheadinget?.text!!.isNotEmpty() &&
                        alertBinding?.sportstypeet?.text!!.isNotEmpty() &&
                        alertBinding?.groundaddresset?.text!!.isNotEmpty() &&
                        alertBinding?.maplinket?.text!!.isNotEmpty() &&
                        alertBinding?.pincodeet?.text!!.isNotEmpty() &&
                        alertBinding?.facilitieset?.text!!.isNotEmpty() &&
                        alertBinding?.venueet?.text!!.isNotEmpty() &&
                        alertBinding?.venueruleset?.text!!.isNotEmpty() &&
                        alertBinding?.priceperhouret?.text!!.isNotEmpty()

                val isFormComplete = allFieldsFilled && isFormValid
                Log.d("status check","status check--->${errors.values}")
                // If the form is valid, call the ViewModel function to make the API call
                if (isFormComplete) {
                    // You can call the API method from ViewModel here
                    Log.d("status check","status check--->${isFormComplete}")
                    alertBinding?.apply {
                       // dialog.dismiss()
                        viewModel.postGroundData(
                            priceperhouret.text.toString(),
                            venueruleset.text.toString(),
                            venueet.text.toString(),
                            facilitieset.text.toString(),
                            pincodeet.text.toString(),
                            areaId,
                            cityId,
                            maplinket.text.toString(),
                            groundaddresset.text.toString(),
                            sportstypeet.text.toString(),
                            groundheadinget.text.toString(),imageNamesList,addImagesList,selectedTimeSlots)

                    }
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

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, citiesSpinnerList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            alertBinding?.citySpinner?.adapter = adapter
        }
        @SuppressLint("SetTextI18n")
        private fun handleGetGroundSuccess(data: GetGroundsResponse?) {

            Log.d("get handleGetGroundSuccess","response--->${data}")
            gropundList.clear()
            gropundList.addAll(data?.grounds as ArrayList<GetGround>)
            binding.groundcount.text= "("+gropundList.size.toString()+")"

            groundAdapter.update(gropundList)

        }
        @SuppressLint("SetTextI18n")
        private fun handlePostgroundSuccess(data: PostGroundsResponse?) {
            dialog.dismiss()
            binding.groundcount.text= "("+gropundList.size.toString()+")"
            Log.d("get handleGetGroundSuccess","response--->${data?.ground?.areaName}")
            groundAdapter.addItem(data?.ground!!)


        }
        private fun handleDeleteGroundSuccess(data: String) {
            Log.e(" handleDeleteGroundSuccess", "---> $data")
           // groundAdapter.deleteItem()
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

        private fun handleLoginError(message: String?) {
            Log.e(" failed", "---> $message")
            Singleton.showToast(requireContext(), message ?: "Error occurred")
        }

        private fun setUI() {
            binding.apply {

                btnAddItem.setDebouncedClickListener {
                    alertDialogAddGround()
                }
                btnTryAgain.setDebouncedClickListener {
                    apiCall()
                }
            }
        }


        private fun setCitySpinners() {
            viewModel.getCities()
        }

        private fun setAreaSpinners() {
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, areasSpinnerList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            alertBinding?.areaSpinner?.adapter = adapter
        }
        private fun setRecycleview() {
            addImagesAdapter = ImagesAdapterUpdate(requireContext(), addImagesList,imageNamesList)
            alertBinding?.groundRv?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            alertBinding?.groundRv?.adapter = addImagesAdapter
        }

        // Callback for the permission request
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // Permission denied, show a message to the user
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissions[0])) {
                        // The user has denied the permission but has not checked "Don't ask again"
                        Toast.makeText(context, "Permission is required to access gallery images", Toast.LENGTH_SHORT).show()
                    } else {
                        // The user has permanently denied the permission (checked "Don't ask again")
                        // You can provide an option to open the settings page
                        Toast.makeText(context, "Permission denied. Please enable it in the app settings.", Toast.LENGTH_LONG).show()

                        // Optionally, you could guide them to the app settings screen
                        // Open app settings (e.g., to let the user enable permissions)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            }
            else if(requestCode == LOCATION_REQUEST_CODE){
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Log.d("Permissions", "Location permission granted")
                  //  getCurrentLocation()
                } else {
                    Log.e("Permissions", "Location permission denied")
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                    AlertDialog.Builder(requireContext())
                        .setTitle("Permission Required")
                        .setMessage("Location access is required for this feature. Please enable it in Settings.")
                        .setPositiveButton("Open Settings") { _, _ ->
                            openAppSettings()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
        private fun openAppSettings() {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            }
            startActivity(intent)
        }
        private fun openGalleryForImages() {
            Log.d("TAG", "---> openGalleryForImages")
            //currentImageCount=0
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(
                Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST
            )
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == PICK_IMAGES_REQUEST) {
                    // Check if the data contains multiple images
                    data?.let {
                        if (it.clipData != null) {
                            // Multiple images selected
                            val count = it.clipData!!.itemCount
                            for (i in 0 until count) {
                                val imageUri = it.clipData!!.getItemAt(i).uri
                                addImagesList.add(imageUri)
                                val imageName = getFileName(imageUri, requireContext().contentResolver)
                                imageNamesList.add(imageName)
                                // Handle the selected image URI (e.g., display, upload, etc.)
                                Log.d("TAG", "Selected Image URI: $imageUri")
                                // You can add the URIs to a list or perform any other actions here
                            }
                        } else {
                            // Single image selected
                            val imageUri = it.data
                            imageUri?.let {
                                addImagesList.add(it)
                                imageNamesList.add(getFileName(it, requireContext().contentResolver))
                            }

                            Log.d("TAG", "Selected Image URI: $imageUri")
                            // Handle the selected image URI (e.g., display, upload, etc.)
                        }
                        addImagesAdapter.notifyDataSetChanged()
                    }
                }
            }
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

                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
            else {

            }

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
                           alertBinding?.loadingProgressBar?.visibility=View.GONE
                            mapsLink=getMapLinkFromLocation(location.latitude, location.longitude)
                            alertBinding?.maplinket?.setText(mapsLink)
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

        private fun alertDialogAddGround() {
            imageNamesList.clear()
            addImagesList.clear()
            alertBinding = FragmentAddGroundBinding.inflate(LayoutInflater.from(requireContext()))

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(alertBinding!!.root)
            dialog = builder.create()
            setRecycleview()
            setCitySpinners()

            // List of all checkboxes
            // Map time slots to the corresponding checkboxes
            // List of time slots corresponding to the checkboxes
            val timeSlots = listOf(
                "01:00AM - 02:00AM", "02:00AM - 03:00AM", "03:00AM - 04:00AM", "04:00AM - 05:00AM",
                "05:00AM - 06:00AM", "06:00AM - 07:00AM", "07:00AM - 08:00AM", "08:00AM - 09:00AM",
                "09:00AM - 10:00AM", "10:00AM - 11:00AM", "11:00AM - 12:00PM", "12:00PM - 01:00PM",
                "01:00PM - 02:00PM", "02:00PM - 03:00PM", "03:00PM - 04:00PM", "04:00PM - 05:00PM",
                "05:00PM - 06:00PM", "06:00PM - 07:00PM", "07:00PM - 08:00PM", "08:00PM - 09:00PM",
                "09:00PM - 10:00PM", "10:00PM - 11:00PM", "11:00PM - 12:00AM", "12:00AM - 01:00AM"
            )
            val checkBoxes = listOf(
                alertBinding!!.checkBox1, alertBinding!!.checkBox2, alertBinding!!.checkBox3, alertBinding!!.checkBox4,
                alertBinding!!.checkBox5, alertBinding!!.checkBox6, alertBinding!!.checkBox7, alertBinding!!.checkBox8,
                alertBinding!!.checkBox9, alertBinding!!.checkBox10, alertBinding!!.checkBox11, alertBinding!!.checkBox12,
                alertBinding!!.checkBox1PM, alertBinding!!.checkBox2PM, alertBinding!!.checkBox3PM, alertBinding!!.checkBox4PM,
                alertBinding!!.checkBox5PM, alertBinding!!.checkBox6PM, alertBinding!!.checkBox7PM, alertBinding!!.checkBox8PM,
                alertBinding!!.checkBox9AM, alertBinding!!.checkBox10AM, alertBinding!!.checkBox11AM, alertBinding!!.checkBox12PM
            )




            // Set the dialog to full screen
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            alertBinding?.apply {
                addimagetv.setDebouncedClickListener {
                    openGalleryForImages()
                }
                citySpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
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
                areaSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                        // val selectedCityName = parent?.getItemAtPosition(position).toString()
                        val selectedArea = areasList?.get(position)
                        areaId = selectedArea!!.id.toString()

                        Log.d("area id","area id ---${areaId}")
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                btnsaveground.setDebouncedClickListener {
                    // Create a list to store selected time slots

                    selectedTimeSlots.clear() // Clear previous selections before adding new ones
                    checkBoxes.forEachIndexed { index, checkBox ->
                        if (checkBox.isChecked) {
                            val timeParts = timeSlots[index].split(" - ")
                            if (timeParts.size == 2) {
                                selectedTimeSlots.add(Slot(timeParts[1], timeParts[0])) // Store as object
                            }
                        }
                    }
                    val gson = Gson()

                    val jsonFormat = gson.toJson(selectedTimeSlots)


                    Log.d("time slots", "time slot --> ${addImagesList.size}")


                    if(selectedTimeSlots.isNotEmpty())
                    {

                        if(addImagesList.size >0 && imageNamesList.size >0)
                        {
                            viewModel.validInputs(priceperhouret.text.toString(),venueruleset.text.toString(),venueet.text.toString(),
                                facilitieset.text.toString(),pincodeet.text.toString(),areaId,cityId,maplinket.text.toString(),groundaddresset.text.toString(),sportstypeet.text.toString(),
                                groundheadinget.text.toString())
                        }else{
                            Toast.makeText(requireContext(), "Please Upload Images", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(requireContext(), "Please select at least one time slot", Toast.LENGTH_SHORT).show()
                    }

                }
                cityArow.setDebouncedClickListener {
                    citySpinner.performClick()
                }
                areaArow.setDebouncedClickListener {
                    areaSpinner.performClick()
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
                crossIcon.setDebouncedClickListener {
                    dialog.dismiss()
                }
            }
            // Optional: Remove default corner dimming (if needed)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
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

        // Extension function to make the View gone
        fun View.gone() {
            this.visibility = View.GONE
        }


        private fun getFileName(uri: Uri, resolver: ContentResolver): String {
            var name = "unknown_file"
            val cursor = resolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = it.getString(nameIndex)
                    }
                }
            }
            return name
        }
//        override fun onResume() {
//            super.onResume()
//            // Show BottomNavigationView again when coming back to the bottom navigation fragments
//            requireActivity().findViewById<BottomNavigationView>(com.wingspan.groundowner.R.id.bottomNavigationView).visibility = View.VISIBLE
//        }
//
//        override fun onPause() {
//            super.onPause()
//            // Hide BottomNavigationView when navigating away
//            requireActivity().findViewById<BottomNavigationView>(com.wingspan.groundowner.R.id.bottomNavigationView).visibility = View.VISIBLE
//        }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
            // Only clear lists if they are initialized
            citiesList?.clear()
            areasList?.clear()

            // Avoid memory leaks by nullifying the lists
            citiesList = null
            areasList = null
        }
}
