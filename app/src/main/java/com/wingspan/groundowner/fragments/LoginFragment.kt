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

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var mobileNumber:String

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
        // Initialize ViewModel with Factory
        setUI()
        setObservers()
    }

    private fun setObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(context, "Verification Successful", Toast.LENGTH_SHORT).show()
                    Log.d("Login","--->Verification Successful")
                    val bundle = Bundle().apply {
                        putString("mobilenumber", mobileNumber)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_otpVerificationFragment,bundle)
                }
                is Resource.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    Log.e("Error","--->${it.message}")
                }
                is Resource.Loading -> {
                    Log.d("Loading", "---> Verification in...")
                }


            }
        }
        viewModel.isDataValid.observe(viewLifecycleOwner){isValid->
            if (isValid) handleLogin()
        }
    }

    private fun handleLogin() {
        if (Singleton.isNetworkAvailable(requireContext())) {

            mobileNumber.let { viewModel.login(it) }
        } else {
            Singleton.showNetworkAlertDialog(requireContext())
        }
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