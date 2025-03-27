package com.wingspan.groundowner.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentLoginBinding
import com.wingspan.groundowner.databinding.FragmentOTPVerificationBinding
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OTPVerificationFragment : Fragment() {
    private var _binding: FragmentOTPVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    lateinit var  pin:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentOTPVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setObservers()
    }

    private fun setObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(context, "otp verification Successful", Toast.LENGTH_SHORT).show()
                    Log.d("Login","--->otp verification Successful")

                }
                is Resource.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    Log.e("Error","--->${it.message}")
                }
                is Resource.Loading -> {
                    Log.d("Loading", "---> Logging in...")
                }


            }
        }
        viewModel.isDataValid.observe(viewLifecycleOwner){isValid->
            if (isValid) handleLogin()
        }
    }

    private fun handleLogin() {
        if (Singleton.isNetworkAvailable(requireContext())) {

            pin.let { viewModel.login(it) }
        } else {
            Singleton.showNetworkAlertDialog(requireContext())
        }
    }

    private fun setUI() {

    binding.apply {

        binding.verify.setDebouncedClickListener {
            pin=binding.pinView.text.toString()
            viewModel.isPinValidation(pin)
        }
        binding.verify.setDebouncedClickListener {
            Singleton.hideKeyboard(requireContext(), requireView())
            binding.apply {
                pin = pinView.text.toString()
                // Validate input before calling API
                viewModel.isPinValidation(pin)
            }

        }
        resendOtp.setDebouncedClickListener {
           // viewModel.resendOtpApiCall(mobileNumber)
        }
        close.setDebouncedClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}