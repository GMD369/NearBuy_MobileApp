package com.nearbuy.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentHomeBinding
import com.nearbuy.app.ui.adapter.ListingAdapter
import com.nearbuy.app.ui.auth.AuthViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel  by activityViewModels()
    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChips()
        setupSearch()
        observeData()

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.loadListings()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.fabToTop.setOnClickListener {
            binding.rvListings.smoothScrollToPosition(0)
        }

        binding.rvListings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy > 10) binding.fabToTop.show() else if (dy < -10) binding.fabToTop.hide()
            }
        })
    }

    private fun setupRecyclerView() {
        val app  = requireActivity().application as NearBuyApplication
        val repo = app.listingRepository
        val userId = app.sessionManager.userId

        listingAdapter = ListingAdapter(
            onItemClick = { listing ->
                val action = HomeFragmentDirections.actionNavHomeToNavDetail(listing.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { listing ->
                val currentUserId = app.sessionManager.userId
                if (app.sessionManager.isLoggedIn) {
                    repo.toggleFavorite(currentUserId, listing.id)
                    listingAdapter.updateFavorites(repo.getFavorites(currentUserId))
                }
            }
        )
        listingAdapter.updateFavorites(repo.getFavorites(userId))
        binding.rvListings.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = listingAdapter
        }
    }

    private fun setupChips() {
        val categories = homeViewModel.getCategories()
        binding.chipGroupCategories.removeAllViews()
        categories.forEachIndexed { index, cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked   = index == 0
                setOnCheckedChangeListener { _, checked ->
                    if (checked) homeViewModel.filterByCategory(cat)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                homeViewModel.search(query.orEmpty())
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) homeViewModel.search("")
                return true
            }
        })
    }

    private fun observeData() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.shimmerView.visibility = View.VISIBLE
                binding.shimmerView.startShimmer()
                binding.rvListings.visibility = View.GONE
            } else {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
                binding.rvListings.visibility = View.VISIBLE
            }
        }

        homeViewModel.listings.observe(viewLifecycleOwner) { listings ->
            listingAdapter.submitList(listings)
            binding.layoutEmpty.visibility = if (listings.isEmpty()) View.VISIBLE else View.GONE
            binding.layoutError.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadListings()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
