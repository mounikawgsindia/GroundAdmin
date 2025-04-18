package com.wingspan.groundowner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentLoginBinding
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.viewmodel.AuthViewModel

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mobileNumber:String

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        setObservers()
    }


    private fun setObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleLoginSuccess(resource.data?.message)
                is Resource.Error -> handleLoginError(resource.message)
                is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
            }
        }

        viewModel.isDataValid.observe(viewLifecycleOwner) { isValid ->
            if (isValid) handleLogin() else Singleton.showToast(requireContext(), "Invalid Mobile Number")
        }
    }

    private fun handleLogin() {
        if (Singleton.isNetworkAvailable(requireContext())) {
            mobileNumber.let { viewModel.login("+91$it") }
        } else {
            Singleton.showNetworkAlertDialog(requireContext())
        }
    }
    private fun handleLoginSuccess(message: String?) {
        Toast.makeText(context, "Verification Successful ${message}", Toast.LENGTH_SHORT).show()
        Log.d("Login", "---> Verification Successful")
        Singleton.showCustomSnackbar(requireContext(), message ?: "Success", R.color.green)

        val bundle = Bundle().apply {
            putString("mobilenumber", mobileNumber)
        }
        findNavController().navigate(R.id.action_loginFragment_to_otpVerificationFragment, bundle)
    }

    private fun handleLoginError(message: String?) {
        Log.e("Login failed", "---> $message")
        Singleton.showToast(requireContext(), message ?: "Error occurred")
        Singleton.showCustomSnackbar(requireContext(), message ?: "Error", R.color.light_red)
    }


    private fun setUI() {


        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        binding.btnSendOtp.setOnClickListener {
            Singleton.hideKeyboard(requireContext(), requireView())
            binding.apply {
                mobileNumber = etMobileNumber.text.toString()
                // Validate input before calling API
                viewModel.isValidInputs(mobileNumber)
            }

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}