    package com.wingspan.groundowner.fragments

    import android.os.Bundle
    import android.text.Editable
    import android.text.TextWatcher
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.activity.OnBackPressedCallback
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.lifecycleScope
    import androidx.lifecycle.repeatOnLifecycle
    import androidx.navigation.NavOptions
    import androidx.navigation.fragment.findNavController

    import com.wingspan.groundowner.R
    import com.wingspan.groundowner.databinding.FragmentLoginBinding
    import com.wingspan.groundowner.utils.AppLogger
    import com.wingspan.groundowner.utils.Resource
    import com.wingspan.groundowner.utils.Singleton
    import com.wingspan.groundowner.utils.UserPreferences
    import com.wingspan.groundowner.viewmodel.AuthViewModel
    import com.wingspan.groundowner.viewmodel.TrainerViewModel

    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.launch
    import javax.inject.Inject


    @AndroidEntryPoint
    class LoginFragment : Fragment() {

        private var _binding: FragmentLoginBinding? = null
        private val binding get() = _binding!!

        private lateinit var mobileNumber:String
        private val viewModel: AuthViewModel by viewModels()
        private val trainerViewModel: TrainerViewModel by viewModels()
        @Inject
        lateinit var sharedPreferences: UserPreferences
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

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
                OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }

            })

        }



        private fun setObservers() {
            viewModel.loginStatus.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                        is Resource.Success -> handleLoginSuccess(resource.data?.message)
                        is Resource.Error -> handleLoginError(resource.message)
                        is Resource.Loading -> {
                            binding.progressBar.visibility=View.VISIBLE
                            binding.btnSendOtp.isEnabled = false
                        }
                    }

            }

            viewModel.isDataValid.observe(viewLifecycleOwner) {
                isValid ->
                if (isValid) handleLogin() else Singleton.showToast(requireContext(), "Invalid Mobile Number")
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    trainerViewModel.loginTrainer.collect { state ->
                        when (state) {
                            is Resource.Loading -> { binding.progressBar.visibility=View.VISIBLE
                                binding.btnSendOtp.isEnabled = false}
                            is Resource.Success -> {
                                binding.progressBar.visibility=View.GONE
                                binding.btnSendOtp.isEnabled = true
                                Singleton.showToast(requireContext(), state.message ?: "OTP sent successfully")
                                Log.d("Login data","--${state.data}")
                                navigateToOTPVerify()
                            }
                            is Resource.Error -> {
                                binding.progressBar.visibility=View.GONE
                                binding.btnSendOtp.isEnabled = true
                                Singleton.showToast(requireContext(), state.message.toString())
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        private fun handleLogin() {
            if (Singleton.isNetworkAvailable(requireContext())) {
                mobileNumber.let {
                    if(sharedPreferences.getUserType()=="trainer"){
                        trainerViewModel.loginTrainer(it)
                    }else{
                        viewModel.login("+91$it")
                    }

                }
            } else {
                Singleton.showNetworkAlertDialog(requireContext())
            }
        }
        private fun handleLoginSuccess(message: String?) {
            binding.progressBar.visibility=View.GONE
            binding.btnSendOtp.isEnabled = true
            Toast.makeText(context, "Verification Successful ${message}", Toast.LENGTH_SHORT).show()
            Log.d("Login", "---> Verification Successful")
            Singleton.showCustomSnackbar(requireContext(), message ?: "Success", R.color.green)
                navigateToOTPVerify()


        }

        private fun navigateToOTPVerify() {
            val bundle = Bundle().apply {
                putString("mobilenumber", mobileNumber)
            }
            findNavController().navigate(R.id.action_loginFragment_to_otpVerificationFragment, bundle)
        }

        private fun handleLoginError(message: String?) {
            binding.progressBar.visibility=View.GONE
            binding.btnSendOtp.isEnabled = true
            AppLogger.e("Login failed", "---> $message")
            Singleton.showToast(requireContext(), message ?: "Error occurred")
            Singleton.showCustomSnackbar(requireContext(), message ?: "Error", R.color.light_red)
        }


        private fun setUI() {
            binding.etMobileNumber.setText("+91")
            binding.etMobileNumber.setSelection(binding.etMobileNumber.text.length)
            binding.etMobileNumber.addTextChangedListener(object : TextWatcher {
                private val prefix = "+91"

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (!s.toString().startsWith(prefix)) {
                        binding.etMobileNumber.setText(prefix)
                        binding.etMobileNumber.setSelection(prefix.length)
                    }
                }
            })

            binding.register.setOnClickListener {
                if(sharedPreferences.getUserType().equals("trainer")){

                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true) // <-- This will remove AddGroundFragment
                        .build()
                    findNavController().navigate(R.id.action_loginFragment_to_trainerregistration,null,navOptions)
                }else{
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true) // <-- This will remove AddGroundFragment
                        .build()
                    findNavController().navigate(R.id.action_loginFragment_to_registrationFragment,null,navOptions)
                }

            }
            binding.btnSendOtp.setOnClickListener {
                Singleton.hideKeyboard(requireContext(), requireView())
                binding.apply {
                    mobileNumber = etMobileNumber.text.toString()
                    viewModel.isValidInputs(mobileNumber)
                }

            }

        }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }


    }