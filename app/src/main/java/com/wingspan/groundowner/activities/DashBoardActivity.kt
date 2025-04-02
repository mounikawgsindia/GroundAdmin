package com.wingspan.groundowner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
            when(menuItem.itemId){
                R.id.nav_dashboard->navController.navigate(R.id.dashBoardFragment)
                R.id.nav_grounds -> navController.navigate(R.id.groundsFragemnt)
                R.id.nav_payments -> navController.navigate(R.id.paymentsFragment)

            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}