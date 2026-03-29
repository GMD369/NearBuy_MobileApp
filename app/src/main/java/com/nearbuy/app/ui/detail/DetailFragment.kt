package com.nearbuy.app.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.databinding.FragmentDetailBinding
import com.nearbuy.app.ui.adapter.ImageCarouselAdapter
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo    = (requireActivity().application as NearBuyApplication).listingRepository
        val listing = repo.getListingById(args.listingId)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        if (listing == null) {
            Toast.makeText(requireContext(), "Listing not found", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        populateDetails(listing)
        setupImageCarousel(listing)
    }

    private fun populateDetails(listing: Listing) {
        val app = requireActivity().application as NearBuyApplication
        val repo = app.listingRepository
        val session = app.sessionManager

        val formatted = NumberFormat.getNumberInstance(Locale("en", "PK"))
            .format(listing.price.toLong())
        binding.tvDetailPrice.text       = "Rs. $formatted"
        binding.tvDetailTitle.text       = listing.title
        binding.tvDetailLocation.text    = listing.location
        binding.tvDetailCondition.text   = listing.condition
        binding.tvSellerPhoneDetail.text = listing.sellerPhone
        binding.tvDetailDescription.text = listing.description.ifBlank { "No description provided." }
        binding.tvSellerName.text        = listing.sellerName
        binding.tvSellerRating.text      = "★ 4.5 Rating"

        // Save / unsave toggle
        val userId = session.userId
        fun refreshSaveIcon() {
            val isSaved = listing.id in repo.getFavorites(userId)
            binding.fabSave.setImageResource(
                if (isSaved) R.drawable.ic_bookmark_on
                else R.drawable.ic_bookmark_off
            )
        }
        refreshSaveIcon()
        binding.fabSave.setOnClickListener {
            if (session.isLoggedIn) {
                repo.toggleFavorite(userId, listing.id)
                refreshSaveIcon()
            } else {
                Toast.makeText(requireContext(), "Sign in to save items", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnViewSellerProfile.setOnClickListener {
            if (listing.sellerId == session.userId) {
                findNavController().navigate(R.id.nav_profile)
            } else {
                Toast.makeText(requireContext(), "Seller profiles coming soon!", Toast.LENGTH_SHORT).show()
            }
        }

        // Hide Message and Make Offer buttons as requested
        binding.btnMessageSeller.visibility = View.GONE
        binding.btnMakeOffer.visibility = View.GONE

        /*
        binding.btnMessageSeller.setOnClickListener {
            Toast.makeText(requireContext(), "Messaging coming soon!", Toast.LENGTH_SHORT).show()
        }
        binding.btnMakeOffer.setOnClickListener {
            Toast.makeText(requireContext(), "Offer sent! (stored locally)", Toast.LENGTH_SHORT).show()
        }
        */
    }

    private fun setupImageCarousel(listing: Listing) {
        val imagePaths = listing.imagePaths
        if (imagePaths.isEmpty()) {
            binding.viewPagerImages.visibility = View.GONE
            binding.tabDots.visibility = View.GONE
            return
        }
        val adapter = ImageCarouselAdapter(imagePaths)
        binding.viewPagerImages.adapter = adapter
        TabLayoutMediator(binding.tabDots, binding.viewPagerImages) { _, _ -> }.attach()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
