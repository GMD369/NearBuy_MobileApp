package com.nearbuy.app.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.nearbuy.app.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostStepAdapter()
        binding.postViewPager.adapter = adapter
        binding.postViewPager.isUserInputEnabled = false // Disable swiping to force buttons

        binding.btnNext.setOnClickListener {
            val currentItem = binding.postViewPager.currentItem
            if (validateStep(currentItem)) {
                if (currentItem < adapter.itemCount - 1) {
                    binding.postViewPager.currentItem = currentItem + 1
                } else {
                    Toast.makeText(requireContext(), "Listing Posted Successfully!", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            val currentItem = binding.postViewPager.currentItem
            if (currentItem > 0) {
                binding.postViewPager.currentItem = currentItem - 1
            }
        }

        binding.postViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.postProgress.progress = (position + 1) * 25
                binding.btnBack.visibility = if (position > 0) View.VISIBLE else View.GONE
                binding.btnNext.text = if (position == adapter.itemCount - 1) "Post" else "Next"
            }
        })
    }

    private fun validateStep(step: Int): Boolean {
        // Basic validation logic
        return when (step) {
            1 -> {
                // Check title if available (needs access to ViewHolder)
                // For now, allow navigation but real apps would check binding.etTitle
                true 
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
