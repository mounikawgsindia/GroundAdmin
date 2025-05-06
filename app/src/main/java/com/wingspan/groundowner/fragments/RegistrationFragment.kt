package com.wingspan.groundowner.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentRegistrationBinding
import com.wingspan.groundowner.utils.Resource
import com.wingspan.groundowner.utils.Singleton
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var mobileNumber:String
    private lateinit var email:String
    private lateinit var username:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setObservers()

    }


    private fun setObservers() {
        viewModel.registrationStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> handleLoginSuccess(resource.data?.message)
                is Resource.Error -> { handleLoginError(resource.message)
                }
                is Resource.Loading -> Log.d("Loading", "---> Verification in progress...")
            }
        }

        viewModel.isDataValid.observe(viewLifecycleOwner) { isValid ->
            if (isValid) handleRegistration()
        }
        viewModel.mobileNumberError.observe(viewLifecycleOwner){error->
            if(error!=null){
                binding.mobileErrorTV.visibility=View.VISIBLE
                binding.mobileErrorTV.text = error
            }
            else{
                binding.mobileErrorTV.visibility=View.GONE
            }
        }
        viewModel.emailError.observe(viewLifecycleOwner){error->
            Log.d("emailError", "---> emailError in progress...$error")
            if(error!=null){
                binding.emailErrorTV.visibility=View.VISIBLE
                binding.emailErrorTV.text = error
            }else{
                binding.emailErrorTV.visibility=View.GONE
            }
        }
        viewModel.usernameError.observe(viewLifecycleOwner){error->
            if(error!=null){
                binding.usernameErrorTV.visibility=View.VISIBLE
                binding.usernameErrorTV.text = error
            }
            else{
                binding.usernameErrorTV.visibility=View.GONE
            }
        }
    }

    private fun handleRegistration() {
        if (Singleton.isNetworkAvailable(requireContext())) {
            viewModel.registration("+91$mobileNumber",email,username)
        } else {
            Singleton.showNetworkAlertDialog(requireContext())
        }
    }
    private fun handleLoginSuccess(message: String?) {
        Toast.makeText(context, "Registration  Successful", Toast.LENGTH_SHORT).show()
        Log.d("Login", "---> Verification Successful ${message}")
        Singleton.showCustomSnackbar(requireContext(), message ?: "Success", R.color.green)
    }

    private fun handleLoginError(message: String?) {
        Log.e("Registration failed", "---> $message")
        Singleton.showToast(requireContext(), message ?: "Error occurred")
        Singleton.showCustomSnackbar(requireContext(), message ?: "Error", R.color.light_red)
    }


    private fun setUI() {


//        binding.registerBtn.setOnClickListener {
//           // findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
//        }
        binding.registerBtn.setOnClickListener {
            Singleton.hideKeyboard(requireContext(), requireView())
            binding.apply {
                mobileNumber = mobilenumberEt.text.toString()
                email=emailEt.text.toString()
                username=usernameEt.text.toString()
                // Validate input before calling API
                viewModel.validRegistration(email,mobileNumber,username)
            }

        }
        binding.signInTV.setDebouncedClickListener {
            findNavController().navigateUp()
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}