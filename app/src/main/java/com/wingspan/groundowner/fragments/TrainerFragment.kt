package com.wingspan.groundowner.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider


import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.wingspan.groundowner.R
import com.wingspan.groundowner.activities.MainActivity
import com.wingspan.groundowner.adapters.ProfileGalleryImageAdapter
import com.wingspan.groundowner.databinding.AlertUpdateProfileLayoutBinding
import com.wingspan.groundowner.databinding.FragmentTrainerBinding
import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wingspan.groundowner.adapters.DaysAdapter
import com.wingspan.groundowner.adapters.ImagesAdapterUpdateTrainer
import com.wingspan.groundowner.model.TrainerProfileResponse
import com.wingspan.groundowner.utils.AppLogger
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.viewmodel.TrainerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.getValue
import kotlin.text.isNullOrEmpty

@AndroidEntryPoint
class TrainerFragment : Fragment() {
    private var _binding: FragmentTrainerBinding? = null
    private val binding get() = _binding!!
    private var alertDialog: AlertDialog? = null
    private var personalImage: Uri? = null
    private lateinit var alertDialogBinding: AlertUpdateProfileLayoutBinding
    private lateinit var dialog: BottomSheetDialog
    private var addImagesList = ArrayList<Uri>()
    private var selectedDaysList = ArrayList<String>()
    private val viewModel: TrainerViewModel by viewModels()
   // private var personalImageUri: Uri? = null
    var fullName = ""
    var specialization = ""
    var address = ""
    var about = ""
    var price = ""
    var slots = ""
    @Inject
    lateinit var sharedPreferences:UserPreferences
    val imageAdapter by lazy { ProfileGalleryImageAdapter(requireContext(),sharedPreferences.getGallery()) }
    val imageAdapterUpdate by lazy { ImagesAdapterUpdateTrainer(requireContext(),addImagesList
    ) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentTrainerBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.green)

        //show 3 dots

        setUI()
        setRecycleview()
        setObserver()
        //convert http images to //image utl
        convertHttpImagesToUrisAndSave(requireContext(),sharedPreferences.getGallery())
        convertHttpImageToUri(requireContext(), "https://example.com/image.jpg") { uri ->
            if (uri != null) {
                personalImage = uri
                Log.d("ImageUri", uri.toString())
            } else {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }

        val activity = requireActivity() as AppCompatActivity

        // Set toolbar as support action bar
        activity.setSupportActionBar(binding.toolbar)

        // Enable back button
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)  // We'll use collapsing toolbar title

        // Set title on collapsing toolbar
       // binding.collapsingToolbar.title = "User Name"

        // Set colors for title in collapsed and expanded state
        binding.collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        // Handle back button click
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.collapsingToolbar.title =sharedPreferences.getUsername()
        // Fade out profile image as toolbar collapses
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScroll = appBarLayout.totalScrollRange
            val percentage = kotlin.math.abs(verticalOffset).toFloat() / totalScroll
            binding.profileImage.alpha = 1 - percentage
        })

        // Add a MenuProvider to handle menu events
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflate your menu resource here
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        Log.d("onOptionsItemSelected","--->menu_settings")
                        showSettingsDialog()
                        true
                    }
                    R.id.menu_logout -> {
                        Log.d("onOptionsItemSelected","--->menu_logout")
                       logout()

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setObserver() {
        viewModel.updateProfileInputsError.observe(viewLifecycleOwner, Observer { errors ->
            alertDialogBinding?.apply {
                // Ground heading error

                if (errors["specialization"].isNullOrEmpty()) {
                    specErrorTV.gone()  // Hide if no error
                } else {
                    specErrorTV.visible()  // Show if error exists
                    specErrorTV.text = errors["specialization"]
                }

                // Sport type error
                if (errors["address"].isNullOrEmpty()) {
                    addressErrorTV.gone()  // Hide if no error
                } else {
                    addressErrorTV.visible()  // Show if error exists
                    addressErrorTV.text = errors["address"]
                }
                if (errors["about"].isNullOrEmpty()) {
                    aboutErrorTV.gone()  // Hide if no error
                } else {
                    aboutErrorTV.visible()  // Show if error exists
                    aboutErrorTV.text = errors["about"]
                }

                // Sport type error
                if (errors["price"].isNullOrEmpty()) {
                    priceErrorTV.gone()  // Hide if no error
                } else {
                    priceErrorTV.visible()  // Show if error exists
                    priceErrorTV.text = errors["price"]
                }
                if (errors["slots"].isNullOrEmpty()) {
                    slotErrorTV.gone()  // Hide if no error
                } else {
                    slotErrorTV.visible()  // Show if error exists
                    slotErrorTV.text = errors["slots"]
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
                        "specialization" -> spectv
                        "address" ->addimagetv
                        "fullName" -> usernametv
                        "about"->abouttv
                        "price"->pricetv
                        "slots"->slotstv
                        "addImagesList" -> addimagetv

                        else -> null
                    }
                    targetView?.requestFocus()
                    targetView?.let {
                        // Optional: scroll if inside ScrollView or NestedScrollView
                        (binding?.root as? ScrollView)?.smoothScrollTo(0, it.top)
                    }
                }

                // Check if all required fields are filled
                val isFormValid = errors["specialization"].isNullOrEmpty() &&
                        errors["address"].isNullOrEmpty() &&
                        errors["fullName"].isNullOrEmpty() &&
                        errors["about"].isNullOrEmpty()&&
                        errors["price"].isNullOrEmpty() &&
                        errors["slots"].isNullOrEmpty() &&
                        errors["addImagesList"].isNullOrEmpty()


                // Check if there are no errors and all fields are filled
                val allFieldsFilled = specEt.text!!.isNotEmpty() &&
                        addressEt.text!!.isNotEmpty() &&
                        aboutEt.text!!.isNotEmpty() &&
                        priceEt.text!!.isNotEmpty() &&
                        slotsEt.text!!.isNotEmpty() &&
                        usernameEt.text!!.isNotEmpty()


                val isFormComplete = allFieldsFilled && isFormValid


                if (isFormComplete) {
                    if(Singleton.isNetworkAvailable(requireContext())){
                        viewModel.updateProfile(requireContext(),address,about,price,slots,specialization,fullName,addImagesList,personalImage,selectedDaysList)
                    }else{
                        Singleton.isNetworkAvailable(requireContext())
                    }

                }
            }

        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateTrainerProfile.collect { state ->
                    when (state) {
                        is Resource.Loading -> { alertDialogBinding.progressBarUpdate.visibility=View.VISIBLE
                            alertDialogBinding.updateBtn.isEnabled = false}
                        is Resource.Success -> {
                            alertDialogBinding.progressBarUpdate.visibility=View.GONE
                            alertDialogBinding.updateBtn.isEnabled = true
                            AppLogger.d("updateTrainerProfile","--->${state.data}")
                            Singleton.showToast(requireContext(), state.message.toString())


                            updateProfile(state.data)
                            //navigateToLogin()

                        }
                        is Resource.Error -> {
                            alertDialogBinding.progressBarUpdate.visibility=View.GONE
                            alertDialogBinding.updateBtn.isEnabled = true
                            Singleton.showToast(requireContext(), state.message.toString())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateProfile(response: TrainerProfileResponse?) {
        var user=response?.trainer
        user?.let {
            sharedPreferences.saveTrainerData(
                it.id.toString(),
                sharedPreferences.getToken(),
                it.fullName,
                it.email,
                it.phoneNumber,
                it.specialization,
                it.profileImage,
                it.gallery,it.address,it.about,it.pricing,it.slots
            )}
        dialog.dismiss()
        updateUI()
    }


    private fun setRecycleview() {
        binding.trainerRv.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter=imageAdapter
        }
    }

    private fun setUI() {
        updateUI()

    }

    private fun updateUI() {
        binding.apply{

            fullnametv.text=sharedPreferences.getUsername()
            numbertv.text=sharedPreferences.getMobileNumber()
            emailtv.text=sharedPreferences.getEmail()
            spectv.text=sharedPreferences.getSpecialization()
            slotstv.text=sharedPreferences.getSlots()
            abouttv.text=sharedPreferences.getAbout()
            addresstv.text=sharedPreferences.getAddress()
            pricetv.text=sharedPreferences.getPrice()

            Glide.with(requireContext())
                .load(sharedPreferences.getProfileImage()).placeholder(R.drawable.profile)
                .into(profileImage)
            setRecycleview()

        }

    }

    private fun setRecycleviewUpdate() {
        alertDialogBinding.updateRv.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter=imageAdapterUpdate
        }
    }



    private fun showSettingsDialog() {
        alertDialogBinding = AlertUpdateProfileLayoutBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(alertDialogBinding.root)
        dialog.setCancelable(true)

        setRecycleviewUpdate()
        alertDialogBinding.apply {
        usernameEt.setText(sharedPreferences.getUsername().toString())
        specEt.setText(sharedPreferences.getSpecialization())
        priceEt.setText(sharedPreferences.getPrice())
        aboutEt.setText(sharedPreferences.getAbout())
         addressEt.setText(sharedPreferences.getAddress())
        slotsEt.setText(sharedPreferences.getSlots())


        Glide.with(requireContext())
                .load(sharedPreferences.getProfileImage()).placeholder(R.drawable.profile)
                .into(profileImageUpdate)

        val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val adapter = DaysAdapter(daysList) { selectedDays ->
            selectedDaysList= selectedDays as ArrayList<String>
            Log.d("SelectedDays", selectedDays.toString())
        }

        dateRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        dateRv.adapter = adapter
        cameraIcon.setOnClickListener {
            openGallery()
        }


        updateBtn.setDebouncedClickListener {
            fullName = usernameEt.text.toString()
            specialization = specEt.text.toString()
            address = addressEt.text.toString()
            about = aboutEt.text.toString()
            price = priceEt.text.toString()
            slots = slotsEt.text.toString()
            viewModel.validUpdateInputs(fullName,specialization,address,about,price,slots,addImagesList)
        }
        addimagetv.setDebouncedClickListener {
             openGalleryForImages()
         }

        }

        dialog.show()

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

                imageAdapterUpdate.notifyDataSetChanged()
            }
        }
    }
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            alertDialogBinding.profileImageUpdate.setImageURI(it) // Show selected image
            personalImage=uri

        }

    }
    private fun logout() {

        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure want to Logout?")
        builder.setPositiveButton("OK"){dialog,which->
            sharedPreferences= UserPreferences(requireContext())
            sharedPreferences.logoutAdmin()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()

        }
        builder.setNegativeButton("CANCEL"){dialog,which->
            dialog.dismiss()
        }
        alertDialog=builder.create()
        alertDialog?.setCancelable(false)
        alertDialog?.show()

    }


    fun convertHttpImagesToUrisAndSave( context: Context,urlList: List<String>) {
        addImagesList.clear()
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("images1","--${urlList}")
            urlList.forEach { imageUrl ->
                try {
                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val inputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    // Save bitmap to cache file
                    val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    // Get Uri from File
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    withContext(Dispatchers.Main) {


                        addImagesList.add(uri)
                        Log.d("images","--${addImagesList.size}")

                    }

                } catch (e: Exception) {
                    Log.e("images3","--${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    fun convertHttpImageToUri(
        context: Context,
        imageUrl: String,
        callback: (Uri?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val file = File(context.cacheDir, "single_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                withContext(Dispatchers.Main) {
                    callback(uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
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