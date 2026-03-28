package com.nearbuy.app.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val noNavDestinations = setOf(
        R.id.nav_login,
        R.id.nav_register,
        R.id.nav_onboarding,
        R.id.nav_detail
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // DO NOT use edge to edge — let system handle insets normally
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val show = destination.id !in noNavDestinations
            binding.bottomNavigation.visibility = if (show) View.VISIBLE else View.GONE
            binding.navDivider.visibility = if (show) View.VISIBLE else View.GONE
        }

        if (savedInstanceState == null) {
            val session = (application as NearBuyApplication).sessionManager
            if (!session.isOnboardingDone) navController.navigate(R.id.nav_onboarding)
        }
    }
}