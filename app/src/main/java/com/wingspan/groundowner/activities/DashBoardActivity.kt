package com.wingspan.groundowner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.ActivityDashBoardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashBoardActivity : AppCompatActivity() {
    private var _binding: ActivityDashBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    val bottomNav get() = binding.bottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController from NavHostFragment inside the FragmentContainerView
        val navHostFragment = binding.fragmentContainerView.getFragment<NavHostFragment>()
        navController = navHostFragment.navController

        // Setup BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Override navigation to avoid multiple copies of the same fragment in the back stack
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.graph.startDestinationId, false) // Keep start destination
                .build()

            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    navController.navigate(R.id.dashBoardFragment, null, options)
                    true
                }
                R.id.nav_grounds -> {
                    navController.navigate(R.id.groundsFragemnt, null, options)
                    true
                }
                R.id.nav_payments -> {
                    navController.navigate(R.id.paymentsFragment, null, options)
                    true
                }
                R.id.nav_booking -> {
                    navController.navigate(R.id.bookingFragment, null, options)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (navController.currentDestination?.id != navController.graph.startDestinationId) {
            Log.d("DashBoardActivity", "Popping back stack")
            navController.popBackStack()
        } else {
            Log.d("DashBoardActivity", "At start destination, finishing activity")
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}