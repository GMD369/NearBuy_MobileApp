package com.nearbuy.app.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.visibility =
                if (destination.id in noNavDestinations) View.GONE else View.VISIBLE
        }

        if (savedInstanceState == null) {
            val session = (application as NearBuyApplication).sessionManager
            if (!session.isOnboardingDone) navController.navigate(R.id.nav_onboarding)
        }
    }
}
