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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  Properly initialize NavController
        val navHostFragment =  binding.fragmentContainerView.getFragment<NavHostFragment>()
        navController = navHostFragment.navController

        //  Setup BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnItemSelectedListener (){menuItem->
           // Avoids creating duplicate fragments.
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.graph.startDestinationId, false)//clear the back stack
                .build()
            when(menuItem.itemId){
                R.id.nav_dashboard ->navController.navigate(R.id.dashBoardFragment,null,options)
                R.id.nav_grounds -> navController.navigate(R.id.groundsFragemnt,null,options)
                R.id.nav_payments -> navController.navigate(R.id.paymentsFragment,null,options)
                R.id.nav_booking -> navController.navigate(R.id.bookingFragment,null,options)

            }
            true
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()

        if (navController.currentDestination?.id != navController.graph.startDestinationId) {
            // If not on start destination, pop the current fragment
            Log.d("if des","-->${navController.currentDestination?.id}....${navController.graph.startDestinationId}")
            navController.popBackStack()
        } else {
            // If on the start destination, exit the app
            Log.d("if else des","-->")
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}