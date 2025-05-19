package com.wingspan.groundowner.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentAddGroundBinding
import com.wingspan.groundowner.databinding.FragmentAddMoreGroundDetailsBinding
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener


class AddMoreGroundDetailsFragment : Fragment() {

    private var _binding: FragmentAddMoreGroundDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentAddMoreGroundDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setObservers()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :
            OnBackPressedCallback(true){
            override fun handleOnBackPressed() {}
            })
    }
    private fun setObservers() {

    }

    private fun setUI() {
        binding.apply {
            backIcon.setDebouncedClickListener {
                findNavController().popBackStack()
            }
            val checkBoxes = listOf(
                binding.checkBox1,
                binding.checkBox2,
                binding.checkBox3,
                binding.checkBox4,
                binding.checkBox5,
                binding.checkBox6,
                binding.checkBox7,
                binding.checkBox8,
                binding.checkBox9,
                binding.checkBox10,
                binding.checkBox11,
                binding.checkBox12,

            )
            alltimeslotstv.setDebouncedClickListener {
                alltimeslotstv.text="UnChecked"
                checkBoxes.forEach { checkBox ->
                    checkBox.isChecked = !checkBox.isChecked
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}