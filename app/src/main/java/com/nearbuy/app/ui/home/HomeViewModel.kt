package com.nearbuy.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.repository.ListingRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as NearBuyApplication).listingRepository

    private val _listings = MutableLiveData<List<Listing>>()
    val listings: LiveData<List<Listing>> = _listings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentCategory = "All"
    private var currentQuery    = ""

    init { loadListings() }

    fun loadListings() {
        _isLoading.value = true
        val result = when {
            currentQuery.isNotBlank() -> repo.searchListings(currentQuery)
            else                      -> repo.getListingsByCategory(currentCategory)
        }
        _listings.value  = result
        _isLoading.value = false
    }

    fun filterByCategory(category: String) {
        currentCategory = category
        currentQuery    = ""
        loadListings()
    }

    fun search(query: String) {
        currentQuery = query
        loadListings()
    }

    fun getCategories(): List<String> = ListingRepository.CATEGORIES
}
