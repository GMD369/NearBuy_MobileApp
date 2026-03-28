package com.nearbuy.app.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.R
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.databinding.FragmentBrowseBinding
import com.nearbuy.app.ui.adapter.ListingAdapter

class BrowseFragment : Fragment() {

    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!

    private lateinit var listingAdapter: ListingAdapter
    private lateinit var repo: com.nearbuy.app.data.repository.ListingRepository
    private var allListings: List<Listing> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repo = (requireActivity().application as NearBuyApplication).listingRepository

        setupRecyclerView()
        setupSearch()
        loadListings()

        binding.btnFilter.setOnClickListener {
            FilterBottomSheet { min, max, category, condition, _ ->
                var results = allListings
                if (min != Double.MIN_VALUE) results = results.filter { it.price >= min }
                if (max != Double.MAX_VALUE) results = results.filter { it.price <= max }
                category?.let { cat -> results = results.filter { it.category.equals(cat, ignoreCase = true) } }
                condition?.let { cond -> results = results.filter { it.condition.equals(cond, ignoreCase = true) } }
                showListings(results)
                binding.layoutActiveFilters.isVisible = results.size != allListings.size
            }.show(childFragmentManager, FilterBottomSheet.TAG)
        }

        binding.btnClearFilters.setOnClickListener {
            showListings(allListings)
            binding.layoutActiveFilters.isVisible = false
        }

        binding.btnResetFiltersEmpty.setOnClickListener {
            showListings(allListings)
            binding.layoutActiveFilters.isVisible = false
        }
    }

    private fun setupRecyclerView() {
        val app    = requireActivity().application as NearBuyApplication
        val userId = app.sessionManager.userId

        listingAdapter = ListingAdapter(
            onItemClick = { listing ->
                val action = BrowseFragmentDirections.actionNavBrowseToNavDetail(listing.id)
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
        binding.rvBrowseListings.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = listingAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val results = if (query.isNullOrBlank()) allListings
                              else repo.searchListings(query)
                showListings(results)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                val results = if (newText.isNullOrBlank()) allListings
                              else repo.searchListings(newText)
                showListings(results)
                return true
            }
        })
    }

    private fun loadListings() {
        binding.shimmerView.isVisible = true
        binding.shimmerView.startShimmer()
        allListings = repo.getAllListings()
        binding.shimmerView.stopShimmer()
        binding.shimmerView.isVisible = false
        showListings(allListings)
    }

    private fun showListings(listings: List<Listing>) {
        listingAdapter.submitList(listings)
        binding.rvBrowseListings.isVisible = listings.isNotEmpty()
        binding.layoutEmpty.isVisible      = listings.isEmpty()
        binding.tvResultCount.text         = getString(R.string.label_items_found, listings.size)
    }

    override fun onResume() { super.onResume(); loadListings() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
