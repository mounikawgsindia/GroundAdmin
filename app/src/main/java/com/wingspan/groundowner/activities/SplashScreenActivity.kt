package com.wingspan.groundowner.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wingspan.groundowner.R
import com.wingspan.groundowner.databinding.ActivityDashBoardBinding
import com.wingspan.groundowner.databinding.ActivitySplashScreenBinding
import com.wingspan.groundowner.utils.UserPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Installing splash screen
        val splashScreen = installSplashScreen()



        // Optional: Customize the splash screen (e.g., adding a delay)
        splashScreen.setKeepOnScreenCondition { true }

        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (sharedPreferences.isLoggedIn()) {
                Intent(this, DashBoardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()

        }, 2000) // 2-second delay
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}