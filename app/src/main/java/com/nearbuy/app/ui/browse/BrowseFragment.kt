package com.nearbuy.app.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.nearbuy.app.R
import com.nearbuy.app.databinding.FragmentBrowseBinding
import com.nearbuy.app.ui.home.ListingAdapter

class BrowseFragment : Fragment() {

    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BrowseViewModel by viewModels()
    private lateinit var listingAdapter: ListingAdapter
    private var isGridView = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupButtons()
        observeViewModel()
        
        updateToggleIcon()
    }

    private fun setupRecyclerView() {
        listingAdapter = ListingAdapter { listing, _ ->
            val action = BrowseFragmentDirections.actionNavBrowseToNavDetail(listing.id)
            findNavController().navigate(action)
        }
        
        binding.rvBrowseListings.apply {
            adapter = listingAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            scheduleLayoutAnimation()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.search(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.search(it) }
                return true
            }
        })
    }

    private fun setupButtons() {
        binding.btnToggleLayout.setOnClickListener {
            toggleLayout()
        }

        binding.btnFilter.setOnClickListener {
            val bottomSheet = FilterBottomSheet.newInstance { min, max, category, condition, swapOnly ->
                viewModel.applyFilters(min, max, category, condition, swapOnly)
            }
            bottomSheet.show(childFragmentManager, FilterBottomSheet.TAG)
        }

        binding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            binding.searchView.setQuery("", false)
        }

        binding.btnResetFiltersEmpty.setOnClickListener {
            viewModel.clearFilters()
            binding.searchView.setQuery("", false)
        }
    }

    private fun toggleLayout() {
        isGridView = !isGridView
        binding.rvBrowseListings.layoutManager = if (isGridView) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }
        updateToggleIcon()
        binding.rvBrowseListings.scheduleLayoutAnimation()
    }

    private fun updateToggleIcon() {
        val iconRes = if (isGridView) {
            android.R.drawable.ic_menu_sort_by_size 
        } else {
            android.R.drawable.ic_dialog_dialer 
        }
        binding.btnToggleLayout.setIconResource(iconRes)
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { listings ->
            listingAdapter.submitList(listings)
            
            // Update Result Count
            binding.tvResultCount.text = "${listings.size} items found"
            
            // Handle Empty State
            if (listings.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvBrowseListings.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvBrowseListings.visibility = View.VISIBLE
            }
        }

        viewModel.activeFilters.observe(viewLifecycleOwner) { filters ->
            updateFilterChips(filters)
        }
    }

    private fun updateFilterChips(filters: List<String>) {
        binding.cgActiveFilters.removeAllViews()
        
        if (filters.isEmpty()) {
            binding.layoutActiveFilters.visibility = View.GONE
        } else {
            binding.layoutActiveFilters.visibility = View.VISIBLE
            filters.forEach { filterText ->
                val chip = Chip(requireContext()).apply {
                    text = filterText
                    isCheckable = false
                    isCloseIconVisible = false // Keep it simple for now
                    setChipBackgroundColorResource(R.color.md_theme_primaryContainer)
                    setTextColor(resources.getColor(R.color.md_theme_onPrimaryContainer, null))
                    chipStrokeWidth = 0f
                    textSize = 11f
                }
                binding.cgActiveFilters.addView(chip)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
