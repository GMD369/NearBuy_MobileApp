package com.nearbuy.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // Handle system bars — status bar top, nav bar bottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.btnSkip.setPadding(
                binding.btnSkip.paddingLeft,
                systemBars.top + 16,
                binding.btnSkip.paddingRight,
                binding.btnSkip.paddingBottom
            )
            binding.bottomBar.setPadding(
                28.dpToPx(),
                0,
                28.dpToPx(),
                systemBars.bottom + 8
            )
            insets
        }

        val onboardingItems = listOf(
            OnboardingItem(
                "Discover NearBuy",
                "Find hidden gems in your neighborhood. Buy and sell with people near you.",
                R.drawable.ic_onboarding_discover
            ),
            OnboardingItem(
                "Swap & Save",
                "No cash? No problem. Propose a trade and swap items you no longer need.",
                R.drawable.ic_onboarding_swap
            ),
            OnboardingItem(
                "Safe & Secure",
                "Chat with verified users and meet in trusted public zones near you.",
                R.drawable.ic_onboarding_secure
            )
        )

        val adapter = OnboardingAdapter(onboardingItems)
        binding.onboardingViewPager.adapter = adapter
        binding.onboardingViewPager.offscreenPageLimit = 1

        TabLayoutMediator(binding.indicator, binding.onboardingViewPager) { _, _ -> }.attach()

        binding.btnNext.setOnClickListener {
            val current = binding.onboardingViewPager.currentItem
            if (current < onboardingItems.size - 1) {
                binding.onboardingViewPager.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }

        binding.onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.btnNext.text =
                    if (position == onboardingItems.size - 1) "Get Started" else "Next"
            }
        })
    }

    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

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