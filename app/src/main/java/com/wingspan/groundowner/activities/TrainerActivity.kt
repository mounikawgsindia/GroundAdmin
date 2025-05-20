package com.wingspan.groundowner.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.ActivityMainBinding
import com.wingspan.groundowner.databinding.ActivityTrainerBinding

import com.wingspan.groundowner.databinding.FragmentPaymentsBinding
import com.wingspan.groundowner.databinding.FragmentTrainerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainerActivity : AppCompatActivity() {

    private var _binding: ActivityTrainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding=ActivityTrainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}