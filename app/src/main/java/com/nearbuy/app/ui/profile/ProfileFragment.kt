package com.nearbuy.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentProfileBinding
import com.nearbuy.app.ui.adapter.ListingAdapter
import com.nearbuy.app.ui.auth.AuthViewModel
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!authViewModel.isLoggedIn()) {
            binding.layoutGuest.visibility = View.VISIBLE
            binding.rvUserListings.visibility = View.GONE
            binding.btnGuestSignIn.setOnClickListener {
                findNavController().navigate(R.id.nav_login)
            }
            return
        }

        setupRecyclerView()
        setupDrawer()
        observeData()
        profileViewModel.loadProfile()
    }

    private fun setupRecyclerView() {
        listingAdapter = ListingAdapter(
            onItemClick = { listing ->
                val action = ProfileFragmentDirections.actionNavProfileToNavDetail(listing.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { listing ->
                profileViewModel.toggleFavorite(listing.id)
            }
        )
        binding.rvUserListings.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = listingAdapter
        }
    }

    private fun setupDrawer() {
        binding.btnOpenDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener { item ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.menu_edit_profile -> {
                    EditProfileBottomSheet().show(childFragmentManager, "edit_profile")
                    true
                }
                R.id.menu_logout -> {
                    authViewModel.logout()
                    
                    // Clear backstack so user can't go back to profile
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()
                    findNavController().navigate(R.id.nav_login, null, navOptions)
                    true
                }
                else -> false
            }
        }
    }

    private fun observeData() {
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe
            binding.tvUserName.text = user.name
            val ratingStr = if (user.rating > 0) "%.1f".format(user.rating) else "New"
            binding.tvUserStats.text = getString(R.string.label_user_stats, user.listingsCount, ratingStr)

            // Phone
            if (user.phone.isNotBlank()) {
                binding.tvUserPhone.text = "📞 ${user.phone}"
                binding.layoutPhone.visibility = View.VISIBLE
            } else {
                binding.layoutPhone.visibility = View.GONE
            }

            // Location
            if (user.location.isNotBlank()) {
                binding.tvUserLocation.text = "📍 ${user.location}"
                binding.layoutLocation.visibility = View.VISIBLE
            } else {
                binding.layoutLocation.visibility = View.GONE
            }

            // Bio
            if (user.bio.isNotBlank()) {
                binding.tvUserBio.text = user.bio
                binding.tvUserBio.visibility = View.VISIBLE
            } else {
                binding.tvUserBio.visibility = View.GONE
            }

            if (user.profileImagePath.isNotBlank()) {
                binding.ivProfileImage.load(File(user.profileImagePath)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                }
            }
        }

        profileViewModel.userListings.observe(viewLifecycleOwner) { listings ->
            if (binding.tabLayout.selectedTabPosition == 0)
                listingAdapter.submitList(listings)
        }

        profileViewModel.savedListings.observe(viewLifecycleOwner) { listings ->
            if (binding.tabLayout.selectedTabPosition == 1)
                listingAdapter.submitList(listings)
        }

        profileViewModel.favoriteIds.observe(viewLifecycleOwner) { ids ->
            listingAdapter.updateFavorites(ids)
        }

        binding.tabLayout.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                when (tab.position) {
                    0 -> listingAdapter.submitList(profileViewModel.userListings.value ?: emptyList())
                    1 -> listingAdapter.submitList(profileViewModel.savedListings.value ?: emptyList())
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
        })
    }

    override fun onResume() {
        super.onResume()
        if (authViewModel.isLoggedIn()) profileViewModel.loadProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
