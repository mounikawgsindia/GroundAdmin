package com.wingspan.groundowner.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.wingspan.groundowner.R
import com.wingspan.groundowner.activities.MainActivity
import com.wingspan.groundowner.databinding.FragmentTrainerBinding
import com.wingspan.groundowner.databinding.FragmentTrainerRegistrationBinding
import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrainerFragment : Fragment() {
    private var _binding: FragmentTrainerBinding? = null
    private val binding get() = _binding!!
    lateinit var alertDialog:AlertDialog

    @Inject
    lateinit var sharedPreferences:UserPreferences
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

        setHasOptionsMenu(true)
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

        // Fade out profile image as toolbar collapses
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScroll = appBarLayout.totalScrollRange
            val percentage = kotlin.math.abs(verticalOffset).toFloat() / totalScroll
            binding.profileImage.alpha = 1 - percentage
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
               Log.d("onOptionsItemSelected","--->menu_settings")
                true
            }
            R.id.menu_logout -> {
                Log.d("onOptionsItemSelected","--->menu_logout")
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}