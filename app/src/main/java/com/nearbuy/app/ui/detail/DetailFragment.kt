package com.nearbuy.app.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.nearbuy.app.R
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.databinding.FragmentDetailBinding
import java.text.NumberFormat
import java.util.Locale
import coil.load

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 300L
            scrimColor = android.graphics.Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listingId = args.listingId
        val listing = MockData.listings.find { it.id == listingId }

        listing?.let {
            setupUI(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupUI(listing: com.nearbuy.app.data.model.Listing) {
        // Image Carousel
        binding.viewPagerImages.adapter = ImageAdapter(listing.images)
        TabLayoutMediator(binding.tabDots, binding.viewPagerImages) { _, _ -> }.attach()

        // Content
        binding.tvDetailPrice.text = formatPrice(listing.price)
        binding.tvDetailTitle.text = listing.title
        binding.tvDetailLocation.text = listing.location
        binding.tvDetailCondition.text = listing.condition
        binding.tvDetailDescription.text = listing.description

        // Seller Info
        binding.tvSellerName.text = listing.user.name
        binding.tvSellerRating.text = "★ ${listing.user.rating} Rating"
        binding.ivSellerProfile.load(listing.user.profileImage) {
            placeholder(android.R.color.darker_gray)
            error(android.R.color.darker_gray)
        }

        // Actions
        binding.btnMessageSeller.setOnClickListener {
            Snackbar.make(binding.root, "Opening chat with ${listing.user.name}...", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnMakeOffer.setOnClickListener {
            showOfferDialog(listing)
        }

        binding.fabFavorite.setOnClickListener {
            val isFavorite = it.tag == "fav"
            
            // Micro-interaction: Heart pop animation
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.heart_pop)
            it.startAnimation(animation)

            if (isFavorite) {
                binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                it.tag = ""
                Snackbar.make(binding.root, "Removed from favorites", Snackbar.LENGTH_SHORT).show()
            } else {
                binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                it.tag = "fav"
                Snackbar.make(binding.root, "Added to favorites", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOfferDialog(listing: com.nearbuy.app.data.model.Listing) {
        val input = EditText(requireContext()).apply {
            hint = "Enter your offer amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(60, 40, 60, 40)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Make an Offer")
            .setMessage("The asking price is ${formatPrice(listing.price)}")
            .setView(input)
            .setPositiveButton("Send Offer") { _, _ ->
                val offer = input.text.toString()
                if (offer.isNotEmpty()) {
                    Snackbar.make(binding.root, "Offer of Rs. $offer sent to seller", Snackbar.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun formatPrice(price: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
        return format.format(price).replace("PKR", "Rs. ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
