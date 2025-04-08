package com.wingspan.groundowner.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.FragmentAddGroundBinding
import com.wingspan.groundowner.databinding.FragmentGroundsFragemntBinding
import com.wingspan.groundowner.utils.Singleton.setDebouncedClickListener


class AddGroundFragment : Fragment() {

    private var _binding: FragmentAddGroundBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentAddGroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setObservers()
    }

    private fun setObservers() {

    }

    private fun setUI() {
        binding.apply {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}