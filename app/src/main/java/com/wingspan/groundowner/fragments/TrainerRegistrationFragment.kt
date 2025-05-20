package com.wingspan.groundowner.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wingspan.groundowner.R
import com.wingspan.groundowner.adapters.TrainerImageAdapter
import com.wingspan.groundowner.databinding.FragmentTrainerRegistrationBinding


import com.wingspan.groundowner.utils.AppLogger
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import com.wingspan.groundowner.viewmodel.TrainerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNullOrEmpty
import kotlin.getValue
import kotlin.text.isNullOrEmpty

@AndroidEntryPoint
class TrainerRegistrationFragment : Fragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
        private var _binding: FragmentTrainerRegistrationBinding? = null
        private val binding get() = _binding!!
        private lateinit var addImagesAdapter: TrainerImageAdapter
        private var addImagesList = ArrayList<Uri>()
    private val viewModel: TrainerViewModel by viewModels()
        private val specializations = listOf("Trainer", "Coach", "Fitness Expert", "Sports Therapist", "Other")
        @Inject
        lateinit var sharedPreferences: UserPreferences
        var phoneNumber=""
        var email=""
        var specification=""
        var fullName=""

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            _binding=FragmentTrainerRegistrationBinding.inflate(layoutInflater,container,false)
            return binding.root
        }


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            //permission result
            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {

                } else {
                    // Permission denied
                    if (shouldShowRequestPermissionRationale(getRequiredPermission())) {
                        Toast.makeText(context, "Permission is required to access gallery images", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Permission denied. Please enable it in the app settings.", Toast.LENGTH_LONG).show()
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            }
            checkAndRequestPermissions()
            setUI()
            setSpinner()
            setRecycleView()
            setObserver()


        }

    private fun setObserver() {
        viewModel.registrationInputsError.observe(viewLifecycleOwner, Observer { errors ->
            binding?.apply {
                // Ground heading error

                if (errors["phoneNumber"].isNullOrEmpty()) {
                    mobileErrorTV.gone()  // Hide if no error
                } else {
                    mobileErrorTV.visible()  // Show if error exists
                    mobileErrorTV.text = errors["phoneNumber"]
                }

                // Sport type error
                if (errors["email"].isNullOrEmpty()) {
                    emailErrorTV.gone()  // Hide if no error
                } else {
                    emailErrorTV.visible()  // Show if error exists
                    emailErrorTV.text = errors["email"]
                }



                // usernameErrorTV  error
                if (errors["fullName"].isNullOrEmpty()) {
                    usernameErrorTV.gone()  // Hide if no error
                } else {
                    usernameErrorTV.visible()  // Show if error exists
                    usernameErrorTV.text = errors["fullName"]
                }

                // Pincode error
                if (errors["addImagesList"].isNullOrEmpty()) {
                    imagesError1TV.gone()  // Hide if no error
                } else {
                    imagesError1TV.visible()  // Show if error exists
                    imagesError1TV.text = errors["addImagesList"]
                }

                // Scroll to the first error field
                val firstErrorKey = errors.entries.firstOrNull { !it.value.isNullOrEmpty() }?.key
                firstErrorKey?.let { key ->
                    val targetView = when (key) {
                        "phoneNumber" -> binding.mobilenametv
                        "email" ->binding.emailtv
                        "fullName" -> binding.usernametv
                        "addImagesList" -> binding.addimagetv

                        else -> null
                    }
                    targetView?.requestFocus()
                    targetView?.let {
                        // Optional: scroll if inside ScrollView or NestedScrollView
                        (binding?.root as? ScrollView)?.smoothScrollTo(0, it.top)
                    }
                }

                // Check if all required fields are filled
                val isFormValid = errors["phoneNumber"].isNullOrEmpty() &&
                        errors["email"].isNullOrEmpty() &&
                        errors["fullName"].isNullOrEmpty() &&
                        errors["addImagesList"].isNullOrEmpty()


                // Check if there are no errors and all fields are filled
                val allFieldsFilled = binding.mobilenumberEt.text!!.isNotEmpty() &&
                        binding.emailEt.text!!.isNotEmpty() &&
                        binding.usernameEt.text!!.isNotEmpty()


                val isFormComplete = allFieldsFilled && isFormValid
                Log.d("status check","status check--->${errors.values}")

                if (isFormComplete) {
                    if(Singleton.isNetworkAvailable(requireContext())){
                        viewModel.postRegistration(phoneNumber,email,specification,fullName,addImagesList)
                    }else{
                        Singleton.isNetworkAvailable(requireContext())
                    }

                }
            }

        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postRegistration.collect { state ->
                    when (state) {
                        is Resource.Loading -> { binding.progressBar.visibility=View.VISIBLE
                            binding.registerBtn.isEnabled = false}
                        is Resource.Success -> {
                            binding.progressBar.visibility=View.GONE
                            binding.registerBtn.isEnabled = true
                            Singleton.showToast(requireContext(), state.message.toString())
                            navigateToLogin()

                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility=View.GONE
                            binding.registerBtn.isEnabled = true
                            Singleton.showToast(requireContext(), state.message.toString())
                        }
                        else -> {}
                    }
                }
            }
        }

    }

    private fun setSpinner() {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, specializations)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.specSpinner.adapter = adapter
        }
        fun setRecycleView(){
            binding.trainerRv.apply {
                setHasFixedSize(true)
                addImagesAdapter = TrainerImageAdapter(requireContext(), addImagesList)
               layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
               adapter = addImagesAdapter
            }
        }
        private fun setUI() {
            binding.apply {
                addimagetv.setDebouncedClickListener {
                    openGalleryForImages()
                }
                registerBtn.setDebouncedClickListener {
                    phoneNumber=mobilenumberEt.text.toString()
                    email=emailEt.text.toString()
                    if (specification == "Other"){
                        specification=otherSpecEt.text.toString()
                    }
                    fullName=usernameEt.text.toString()

                    viewModel.validInputs(phoneNumber,email,fullName,addImagesList)

                }
                signInTV.setDebouncedClickListener {
                    navigateToLogin()
                }
                binding.specSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        specification = specializations[position]
                        if (specification == "Other") {
                            // Show a dialog or EditText for custom input
                            binding.otherSpecEt.visibility=View.VISIBLE
                        }
                        else{
                            binding.otherSpecEt.visibility=View.GONE
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }

            }
        }

    private fun navigateToLogin() {
        var navOption= NavOptions.Builder()
            .setPopUpTo(R.id.trainRegistration,true)
            .build()
        findNavController().navigate(R.id.action_trainRegistration_to_loginFragment,null,navOption)
    }

    private fun checkAndRequestPermissions() {
        val permission = getRequiredPermission()
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {

        } else {
            permissionLauncher.launch(permission)
        }
    }



    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }



    private fun openGalleryForImages() {
        AppLogger.d("TAG", "---> openGalleryForImages")
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pickImagesLauncher.launch(Intent.createChooser(intent, "Select Pictures"))
    }


    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // Handle single or multiple images
            data?.let {
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    val remainingSlots = 6 - addImagesList.size
                    if (remainingSlots <= 0) {
                        Toast.makeText(requireContext(), "You can select up to 6 images only", Toast.LENGTH_SHORT).show()
                        return@let
                    }

                    for (i in 0 until minOf(count, remainingSlots)) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        addImagesList.add(imageUri)

                    }

                    if (count > remainingSlots) {
                        Toast.makeText(requireContext(), "Only $remainingSlots more images can be selected", Toast.LENGTH_SHORT).show()
                    }

                } else if (it.data != null) {
                    if (addImagesList.size >= 6) {
                        Toast.makeText(requireContext(), "You can select up to 6 images only", Toast.LENGTH_SHORT).show()
                        return@let
                    }
                    val imageUri = it.data!!
                    addImagesList.add(imageUri)

                }

                addImagesAdapter.notifyDataSetChanged()
            }
        }
    }



    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    // Extension function to make the View gone
    fun View.gone() {
        this.visibility = View.GONE
    }


    override fun onDestroy() {
            super.onDestroy()
            _binding=null
    }

}

