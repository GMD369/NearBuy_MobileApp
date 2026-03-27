package com.nearbuy.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.nearbuy.app.R
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        setupRecyclerView()
        setupChips()
        setupSearchView()
        setupSwipeRefresh()
        setupScrollBehavior()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        listingAdapter = ListingAdapter { listing, imageView ->
            val extras = FragmentNavigatorExtras(
                imageView to "listing_image_${listing.id}"
            )
            val action = HomeFragmentDirections.actionNavHomeToNavDetail(listing.id)
            findNavController().navigate(action, extras)
        }
        binding.rvListings.apply {
            adapter = listingAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        binding.btnRetry.setOnClickListener {
            viewModel.loadListings()
        }
    }

    private fun setupChips() {
        binding.chipGroupCategories.removeAllViews()
        val allChip = createChip("All", null)
        allChip.isChecked = true
        binding.chipGroupCategories.addView(allChip)

        MockData.categories.forEach { category ->
            val chip = createChip(category.name, category.icon)
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun createChip(label: String, iconRes: Int?): Chip {
        return Chip(requireContext()).apply {
            text = label
            if (iconRes != null) {
                chipIcon = androidx.core.content.ContextCompat.getDrawable(context, iconRes)
            }
            isCheckable = true
            setChipBackgroundColorResource(R.color.chip_background_selector)
            setTextColor(androidx.core.content.ContextCompat.getColorStateList(context, R.color.chip_text_selector))
            setChipIconTintResource(R.color.chip_text_selector)
            chipStrokeWidth = 0f
            
            setOnClickListener {
                if (label == "All") {
                    viewModel.filterByCategory("All")
                } else {
                    viewModel.filterByCategory(label)
                }
            }
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

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadListings(isRefreshing = true)
        }
    }

    private fun setupScrollBehavior() {
        binding.rvListings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 10) {
                    binding.fabToTop.show()
                } else if (dy < -10 || !recyclerView.canScrollVertically(-1)) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        binding.fabToTop.hide()
                    }
                }
            }
        })

        binding.fabToTop.setOnClickListener {
            binding.rvListings.smoothScrollToPosition(0)
            binding.appBarLayout.setExpanded(true, true)
        }
    }

    private fun observeViewModel() {
        viewModel.listings.observe(viewLifecycleOwner) { listings ->
            listingAdapter.submitList(listings)
            binding.swipeRefresh.isRefreshing = false
            
            binding.layoutEmpty.visibility = if (listings.isEmpty()) View.VISIBLE else View.GONE
            binding.rvListings.visibility = if (listings.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.shimmerView.startShimmer()
                binding.shimmerView.visibility = View.VISIBLE
                binding.rvListings.visibility = View.GONE
                binding.layoutEmpty.visibility = View.GONE
                binding.layoutError.visibility = View.GONE
            } else {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
                // rvListings visibility is managed by listing count observation
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = errorMessage
                binding.rvListings.visibility = View.GONE
                binding.layoutEmpty.visibility = View.GONE
                binding.shimmerView.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            } else {
                binding.layoutError.visibility = View.GONE
            }
        }

        viewModel.refreshSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Listings updated", Snackbar.LENGTH_SHORT).show()
                viewModel.onRefreshHandled()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
