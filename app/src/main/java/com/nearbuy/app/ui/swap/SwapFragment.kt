package com.nearbuy.app.ui.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentSwapBinding
import com.nearbuy.app.ui.auth.AuthViewModel
import com.nearbuy.app.ui.adapter.ListingAdapter
import com.google.android.material.tabs.TabLayout

class SwapFragment : Fragment() {

    private var _binding: FragmentSwapBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val swapViewModel: SwapViewModel by viewModels()
    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSwapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!authViewModel.isLoggedIn()) {
            binding.layoutGuestSwap.root.visibility = View.VISIBLE
            binding.swapTabs.visibility = View.GONE
            binding.rvSwapProposals.visibility = View.GONE
            binding.layoutGuestSwap.btnGuestSwapLogin.setOnClickListener {
                findNavController().navigate(R.id.nav_login)
            }
            binding.layoutGuestSwap.btnGuestSwapHome.setOnClickListener {
                findNavController().navigate(R.id.nav_home)
            }
            return
        }

        setupRecyclerView()
        observeViewModel()
        setupTabs()
        
        swapViewModel.loadData()
    }

    private fun setupRecyclerView() {
        val app = requireActivity().application as NearBuyApplication
        val repo = app.listingRepository
        val userId = app.sessionManager.userId

        listingAdapter = ListingAdapter(
            onItemClick = { listing ->
                val action = SwapFragmentDirections.actionNavSwapToNavDetail(listing.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { listing ->
                if (app.sessionManager.isLoggedIn) {
                    repo.toggleFavorite(userId, listing.id)
                    listingAdapter.updateFavorites(repo.getFavorites(userId))
                }
            }
        )

        listingAdapter.updateFavorites(repo.getFavorites(userId))

        binding.rvSwapProposals.apply {
            adapter = listingAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupTabs() {
        binding.swapTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateListForTab(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateListForTab(position: Int) {
        when (position) {
            0 -> showListings(emptyList()) // Received Proposals
            1 -> showListings(emptyList()) // Sent Proposals
            2 -> swapViewModel.mySwapListings.value?.let { showListings(it) }
        }
    }

    private fun showListings(listings: List<com.nearbuy.app.data.model.Listing>) {
        listingAdapter.submitList(listings)
        val isEmpty = listings.isEmpty()
        binding.rvSwapProposals.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.layoutEmptySwap.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun observeViewModel() {
        swapViewModel.mySwapListings.observe(viewLifecycleOwner) { listings ->
            if (binding.swapTabs.selectedTabPosition == 2) showListings(listings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
