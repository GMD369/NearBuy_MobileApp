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
    private var minPrice        = Double.MIN_VALUE
    private var maxPrice        = Double.MAX_VALUE
    private var currentCondition: String? = null
    private var swapOnly        = false

    init { loadListings() }

    fun loadListings() {
        _isLoading.value = true
        var result = when {
            currentQuery.isNotBlank() -> repo.searchListings(currentQuery)
            else                      -> repo.getListingsByCategory(currentCategory)
        }
        if (minPrice != Double.MIN_VALUE) result = result.filter { it.price >= minPrice }
        if (maxPrice != Double.MAX_VALUE) result = result.filter { it.price <= maxPrice }
        currentCondition?.let { cond -> result = result.filter { it.condition.equals(cond, ignoreCase = true) } }
        if (swapOnly) result = result.filter { it.isSwapAllowed }
        _listings.value  = result
        _isLoading.value = false
    }

    fun applyFilter(min: Double, max: Double, category: String?, condition: String?, swapOnly: Boolean) {
        minPrice          = min
        maxPrice          = max
        category?.let     { currentCategory = it }
        currentCondition  = condition
        this.swapOnly     = swapOnly
        loadListings()
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
