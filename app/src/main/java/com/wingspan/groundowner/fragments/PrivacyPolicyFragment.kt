package com.wingspan.groundowner.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.activity.OnBackPressedCallback
import com.wingspan.groundowner.databinding.FragmentPrivacypolicyBinding

class PrivacyPolicyFragment : Fragment() {
    private var _binding: FragmentPrivacypolicyBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacypolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()

        // Add OnBackPressedCallback to intercept the back button press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate up when system back is pressed
                //findNavController().navigateUp()
            }
        })
    }

    private fun setUI() {
        // Enable JavaScript if needed
        binding.apply {
            privacyPolicyWebView.settings.javaScriptEnabled = true

            // Set WebViewClient to handle URL loading within the WebView
            privacyPolicyWebView.webViewClient = WebViewClient()
            val receivedString = arguments?.getString("myKey")
            Log.d("PP","pppp   ${receivedString}")
            // Load the privacy policy URL
            privacyPolicyWebView.loadUrl(receivedString.toString())
            card.setOnClickListener {
             findNavController().navigateUp()
            }
        }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}