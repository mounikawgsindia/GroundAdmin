package com.wingspan.groundowner.fragments

import LoginResponse
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.wingspan.groundowner.activities.DashBoardActivity
import com.wingspan.groundowner.R
import com.wingspan.groundowner.activities.MainActivity
import com.wingspan.groundowner.databinding.FragmentOTPVerificationBinding
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import com.wingspan.groundowner.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OTPVerificationFragment : Fragment() {
    private var _binding: FragmentOTPVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var sharedPreferences: UserPreferences
    lateinit var  pin:String
    private var countDownTimer: CountDownTimer? = null
    lateinit var mobileNumber:String
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
        initUI()
        setObservers()

    }

    private fun setObservers() {
        viewModel.mobilenumberverifyStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> handleSuccess(it.data!!)
                is Resource.Error -> handleError(it.message ?: "An error occurred")
                is Resource.Loading -> Log.d("OTP", "Verifying OTP...")
            }
        }
        viewModel.resendStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), it.data?.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> handleError(it.message ?: "An error occurred")
                is Resource.Loading -> {}
            }
        }
        viewModel.isDataValid.observe(viewLifecycleOwner) { isValid ->

            Log.d("is valid status","is valid -->$isValid")
            if (isValid) verifyOTP() else Singleton.showToast(requireContext(), "Invalid Pin")
        }
    }


    private fun handleSuccess(data: LoginResponse) {
        Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
        Log.d("Resend", "--> Resend ${data}")
        val groundData=data.ground
        sharedPreferences.saveData(data.token,groundData.username,groundData.email,groundData.phoneNumber)
        Log.d("Resend", "--> Resend ${sharedPreferences.getMobileNumber()}")
        val intent = Intent(requireContext(), DashBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.e("OTP Error", message)
    }

    private fun verifyOTP() {
        if (Singleton.isNetworkAvailable(requireContext())) {
            pin.let { viewModel.pinValidation(it,mobileNumber) }
        } else {
            Singleton.showNetworkAlertDialog(requireContext())
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initUI() {
        mobileNumber = arguments?.getString("mobilenumber").toString()

        with(binding) {
            binding.displayText.text="Enter the 6 digits code sent to you at $mobileNumber"
            verify.setDebouncedClickListener {
                Singleton.hideKeyboard(requireContext(), requireView())
                pin = pinView.text.toString()
                viewModel.isPinValidation(pin)
            }

            resendOtp.setDebouncedClickListener {

                viewModel.resend(mobileNumber)
            }


            startOtpResendTimer()
        }
    }

    private fun startOtpResendTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                _binding?.displayTime?.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _binding?.displayTime?.text = "00:00"
                _binding?.resendOtp?.isEnabled = true
                _binding?.resendOtp?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.light_red)
                )
            }
        }.start()
    }






    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()// Cancel timer to prevent accessing null binding
        _binding = null
    }
}