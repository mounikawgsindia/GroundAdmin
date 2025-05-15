package com.wingspan.groundowner.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.ActivityMainBinding
import com.wingspan.groundowner.databinding.FragmentMainChoiceBinding
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener
import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainChoiceFragment : Fragment() {
    private var _binding: FragmentMainChoiceBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: UserPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentMainChoiceBinding.inflate(layoutInflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }

    private fun setUI() {
        binding.apply {
            btnGroundOwner.setDebouncedClickListener {
                   sharedPreferences.saveUser("groundowner")
                findNavController().navigate(R.id.choice_to_Login)
            }
            btnTrainer.setDebouncedClickListener {
                sharedPreferences.saveUser("trainer")
                findNavController().navigate(R.id.choice_to_Login)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}