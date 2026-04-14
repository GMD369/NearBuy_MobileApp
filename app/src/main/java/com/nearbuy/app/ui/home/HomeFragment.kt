package com.nearbuy.app.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.databinding.FragmentHomeBinding
import com.nearbuy.app.ui.adapter.ListingAdapter
import com.nearbuy.app.ui.browse.FilterBottomSheet

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var listingAdapter: ListingAdapter

    // ── System Broadcasting: listens for network connectivity changes ──
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isConnected = isNetworkAvailable()
            if (!isConnected) {
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = "No internet connection"
                binding.rvListings.visibility = View.GONE
            } else {
                binding.layoutError.visibility = View.GONE
                binding.rvListings.visibility = View.VISIBLE
                homeViewModel.loadListings()
            }
        }
    }

    // ── Application Broadcasting: listens for new listing posted ──
    private val listingPostedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            homeViewModel.loadListings()
        }
    }

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

        binding.swipeRefresh.setColorSchemeColors(
            requireContext().getColor(android.R.color.holo_orange_dark)
        )

        setupRecyclerView()
        setupSearch()
        observeData()

        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.loadListings()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.btnFilter.setOnClickListener {
            FilterBottomSheet { min, max, category, condition, swapOnly ->
                homeViewModel.applyFilter(min, max, category, condition, swapOnly)
            }.show(childFragmentManager, FilterBottomSheet.TAG)
        }

        binding.fabToTop.setOnClickListener {
            binding.rvListings.smoothScrollToPosition(0)
        }

        binding.rvListings.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy > 10) binding.fabToTop.show()
                else if (dy < -10) binding.fabToTop.hide()
            }
        })
    }

    private fun setupRecyclerView() {
        val app = requireActivity().application as NearBuyApplication
        val repo = app.listingRepository
        val userId = app.sessionManager.userId

        listingAdapter = ListingAdapter(
            onItemClick = { listing ->
                val action = HomeFragmentDirections.actionNavHomeToNavDetail(listing.id)
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

        binding.rvListings.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = listingAdapter
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
            binding.layoutEmpty.visibility =
                if (listings.isEmpty()) View.VISIBLE else View.GONE
            binding.layoutError.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadListings()

        // Register system broadcast receiver for network changes
        @Suppress("DEPRECATION")
        requireContext().registerReceiver(
            networkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        // Register app broadcast receiver for new listings
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(listingPostedReceiver, IntentFilter("ACTION_LISTING_POSTED"))
    }

    override fun onPause() {
        super.onPause()
        // Unregister both receivers to prevent memory leaks
        requireContext().unregisterReceiver(networkReceiver)
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(listingPostedReceiver)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnected == true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}