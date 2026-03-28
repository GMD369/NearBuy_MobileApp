package com.nearbuy.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onboardingItems = listOf(
            OnboardingItem(
                "Welcome to NearBuy",
                "Discover hidden gems in your own neighborhood. Buy and sell with ease.",
                android.R.drawable.ic_menu_myplaces
            ),
            OnboardingItem(
                "Swap & Save",
                "Don't have cash? No problem! Propose a trade and swap items you no longer need.",
                android.R.drawable.ic_menu_share
            ),
            OnboardingItem(
                "Safe & Secure",
                "Chat with verified users and meet in safe public zones.",
                android.R.drawable.ic_lock_idle_lock
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        binding.onboardingViewPager.adapter = adapter

        TabLayoutMediator(binding.indicator, binding.onboardingViewPager) { _, _ -> }.attach()

        binding.btnNext.setOnClickListener {
            if (binding.onboardingViewPager.currentItem < onboardingItems.size - 1) {
                binding.onboardingViewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }

        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.btnNext.text = if (position == onboardingItems.size - 1) "Get Started" else "Next"
            }
        })
    }

    private fun completeOnboarding() {
        val session = (requireActivity().application as NearBuyApplication).sessionManager
        session.isOnboardingDone = true
        findNavController().navigate(R.id.action_onboarding_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
