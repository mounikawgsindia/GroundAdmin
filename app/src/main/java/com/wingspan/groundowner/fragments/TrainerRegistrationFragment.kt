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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.wingspan.groundowner.adapters.TrainerImageAdapter
import com.wingspan.groundowner.databinding.FragmentMainChoiceBinding
import com.wingspan.groundowner.databinding.FragmentTrainerRegistrationBinding

import com.wingspan.groundowner.fragments.GroundsFragemnt.Companion.REQUEST_PERMISSION_CODE
import com.wingspan.groundowner.utils.AppLogger
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrainerRegistrationFragment : Fragment() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
        private var _binding: FragmentTrainerRegistrationBinding? = null
        private val binding get() = _binding!!
        private lateinit var addImagesAdapter: TrainerImageAdapter
        private var addImagesList = ArrayList<Uri>()
        private val specializations = listOf("Trainer", "Coach", "Fitness Expert", "Sports Therapist", "Other")
        @Inject
        lateinit var sharedPreferences: UserPreferences
        var selectedSpecilazation=""

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

            binding.specSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selected = specializations[position]
                    if (selected == "Other") {
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

            }
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
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        addImagesList.add(imageUri)
                        handleImage(imageUri)
                    }
                } else if (it.data != null) {
                    val imageUri = it.data!!
                    addImagesList.add(imageUri)
                    handleImage(imageUri)
                }
                addImagesAdapter.notifyDataSetChanged()
            }
        }
    }





    private fun handleImage(uri: Uri) {
        // Your custom logic here: add URI to list, display in RecyclerView, etc.
    }
    override fun onDestroy() {
            super.onDestroy()
            _binding=null
    }

}

