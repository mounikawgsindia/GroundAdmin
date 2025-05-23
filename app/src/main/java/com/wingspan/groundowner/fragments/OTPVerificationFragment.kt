package com.wingspan.groundowner.fragments

import com.wingspan.groundowner.model.LoginResponse
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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.wingspan.groundowner.activities.DashBoardActivity
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentOTPVerificationBinding
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import com.wingspan.groundowner.viewmodel.AuthViewModel
import com.wingspan.groundowner.viewmodel.TrainerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OTPVerificationFragment : Fragment() {
    private var _binding: FragmentOTPVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private val trainerViewModel: TrainerViewModel by viewModels()
    @Inject
    lateinit var sharedPreferences: UserPreferences
    lateinit var pin: String
    private var countDownTimer: CountDownTimer? = null
    lateinit var mobileNumber: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOTPVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setObservers()
        // Override system back press
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    R.id.loginFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.otp_verification_fragment, true) // Clear OTP from backstack
                        .setLaunchSingleTop(true) // Prevent re-adding login if already on top
                        .build()
                )
            }
        })


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

            Log.d("is valid status", "is valid -->$isValid")
            if (isValid) verifyOTP() else Singleton.showToast(requireContext(), "Invalid Pin")
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainerViewModel.verifyTrainer.collect { state ->
                    when (state) {
                        is Resource.Loading -> { binding.progressBar.visibility=View.VISIBLE
                            binding.verify.isEnabled = false}
                        is Resource.Success -> {
                            binding.progressBar.visibility=View.GONE
                            binding.verify.isEnabled = true
                            Singleton.showToast(requireContext(), state.data.toString())
                            Log.d("response data", "is valid -->${state.data}")
                            val response = state.data
                            val user = response?.data

                            user?.let {
                                sharedPreferences.saveTrainerData(
                                    it.id.toString(),
                                    response!!.token,
                                    it.fullName,
                                    it.email,
                                    it.phoneNumber,
                                    it.specialization,
                                    it.profileImage,
                                    it.gallery,"","","",""
                                )}

                            navigateToTrainerFragment()

                        }
                        is Resource.Error -> {
                            binding.progressBar.visibility=View.GONE
                            binding.verify.isEnabled = true
                            Singleton.showToast(requireContext(), state.message.toString())
                        }
                        else -> {}
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                trainerViewModel.resendTrainer.collect { state ->
                    when (state) {
                        is Resource.Loading -> { }
                        is Resource.Success -> {

                            Singleton.showToast(requireContext(), state.message.toString())

                        }
                        is Resource.Error -> {

                            Singleton.showToast(requireContext(), state.message.toString())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun navigateToTrainerFragment(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.trainerFragment, true) // <-- This will remove AddGroundFragment
            .build()

        findNavController().navigate(R.id.action_otp_to_trainFragment,null,navOptions)
    }
    private fun handleSuccess(data: LoginResponse) {
        Toast.makeText(requireContext(), data.message, Toast.LENGTH_SHORT).show()
        Log.d("otpsend", "--> otpsend ${data}")
        val groundData = data.ground
        sharedPreferences.saveData(
            data.token,
            groundData.username,
            groundData.email,
            groundData.phoneNumber
        )
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
            pin.let {
                if(sharedPreferences.getUserType().toString()=="trainer"){
                    trainerViewModel.verifyTrainer(it, mobileNumber)
                }else{
                    viewModel.pinValidation(it, mobileNumber)
                }


            }
        } else {
            Singleton.showNetworkAlertDialog(requireContext())
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initUI() {
        mobileNumber = arguments?.getString("mobilenumber").toString()

        with(binding) {
            binding.displayText.text = "Enter the 6 digits code sent to you at $mobileNumber"
            verify.setDebouncedClickListener {
                Singleton.hideKeyboard(requireContext(), requireView())
                pin = pinView.text.toString()
                viewModel.isPinValidation(pin)
            }

            resendOtp.setDebouncedClickListener {
                pinView.setText("")
                if(Singleton.isNetworkAvailable(requireContext())){
                    if(sharedPreferences.getUserType().toString()=="trainer"){
                        trainerViewModel.resendTrainer(mobileNumber)
                    }else{
                        viewModel.resend(mobileNumber)
                    }
                }else{

                }

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