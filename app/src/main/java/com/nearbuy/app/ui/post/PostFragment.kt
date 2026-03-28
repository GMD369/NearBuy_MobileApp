package com.nearbuy.app.ui.post

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentPostBinding
import com.nearbuy.app.ui.auth.AuthViewModel
import java.io.File

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var adapter: PostStepAdapter

    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isEmpty()) return@registerForActivityResult
        val app = requireActivity().application as NearBuyApplication
        val targetDir = app.localStorageManager.getListingImagesDir()
        val remaining = 5 - adapter.selectedImagePaths.size
        uris.take(remaining).forEach { uri ->
            try {
                val fileName = "listing_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}.jpg"
                val target = File(targetDir, fileName)
                requireContext().contentResolver.openInputStream(uri)?.use { input ->
                    target.outputStream().use { output -> input.copyTo(output) }
                }
                adapter.selectedImagePaths.add(target.absolutePath)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to save one image", Toast.LENGTH_SHORT).show()
            }
        }
        adapter.refreshImageStep()
    }

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

        if (!authViewModel.isLoggedIn()) {
            Toast.makeText(requireContext(), "Please login to post a listing", Toast.LENGTH_SHORT).show()
            view.post { findNavController().navigate(R.id.action_nav_post_to_nav_login) }
            return
        }

        adapter = PostStepAdapter(onPickImages = { pickImages.launch("image/*") })
        binding.postViewPager.adapter = adapter
        binding.postViewPager.isUserInputEnabled = false

        binding.btnNext.setOnClickListener {
            val currentItem = binding.postViewPager.currentItem
            if (validateStep(currentItem)) {
                if (currentItem < adapter.itemCount - 1) {
                    binding.postViewPager.currentItem = currentItem + 1
                } else {
                    saveListing()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            val currentItem = binding.postViewPager.currentItem
            if (currentItem > 0) binding.postViewPager.currentItem = currentItem - 1
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
        return when (step) {
            1 -> {
                if (adapter.title.isBlank()) {
                    Toast.makeText(requireContext(), "Please enter a listing title", Toast.LENGTH_SHORT).show()
                    false
                } else true
            }
            else -> true
        }
    }

    private fun saveListing() {
        val app     = requireActivity().application as NearBuyApplication
        val session = app.sessionManager
        val user    = app.userRepository.getUserById(session.userId)

        val listing = adapter.buildListing(
            sellerId     = session.userId,
            sellerName   = session.userName,
            userLocation = user?.location ?: ""
        )

        if (listing == null) {
            Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show()
            binding.postViewPager.currentItem = 1
            return
        }

        app.listingRepository.addListing(listing)
        Toast.makeText(requireContext(), "Listing posted successfully!", Toast.LENGTH_LONG).show()
        view?.post { findNavController().navigate(R.id.action_nav_post_to_nav_home) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
