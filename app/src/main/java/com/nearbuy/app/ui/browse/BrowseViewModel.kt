package com.nearbuy.app.ui.browse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.data.model.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as NearBuyApplication).listingRepository

    private val _searchResults = MutableLiveData<List<Listing>>()
    val searchResults: LiveData<List<Listing>> = _searchResults

    private val _activeFilters = MutableLiveData<List<String>>()
    val activeFilters: LiveData<List<String>> = _activeFilters

    // Filter State
    private var currentQuery: String = ""
    private var minPrice: Double? = null
    private var maxPrice: Double? = null
    private var category: String? = null
    private var condition: String? = null
    private var swapOnly: Boolean = false

    init {
        applyAllFilters()
    }

    fun search(query: String) {
        currentQuery = query
        applyAllFilters()
    }

    fun applyFilters(min: Double, max: Double, cat: String?, cond: String?, swap: Boolean) {
        this.minPrice = min
        this.maxPrice = max
        this.category = cat
        this.condition = cond
        this.swapOnly = swap
        applyAllFilters()
    }

    fun clearFilters() {
        currentQuery = ""
        minPrice = null
        maxPrice = null
        category = null
        condition = null
        swapOnly = false
        applyAllFilters()
    }

    private fun applyAllFilters() {
        viewModelScope.launch {
            val filters = mutableListOf<String>()
            if (currentQuery.isNotBlank()) filters.add("Search: $currentQuery")
            if (category != null && category != "All Categories" && category != "All") filters.add(category!!)
            if (condition != null) filters.add(condition!!)
            if (swapOnly) filters.add("Swap Available")
            
            _activeFilters.value = filters

            val filtered = withContext(Dispatchers.Default) {
                var list = repo.getAllListings()

                // 1. Search Query
                if (currentQuery.isNotBlank()) {
                    val q = currentQuery.lowercase()
                    list = list.filter { 
                        it.title.lowercase().contains(q) || 
                        it.description.lowercase().contains(q) ||
                        it.category.lowercase().contains(q)
                    }
                }

                // 2. Price Range
                minPrice?.let { min -> list = list.filter { it.price >= min } }
                maxPrice?.let { max -> list = list.filter { it.price <= max } }

                // 3. Category
                if (!category.isNullOrEmpty() && category != "All Categories" && category != "All") {
                    list = list.filter { it.category.equals(category, ignoreCase = true) }
                }

                // 4. Condition
                if (!condition.isNullOrEmpty()) {
                    list = list.filter { it.condition.contains(condition!!, ignoreCase = true) }
                }

                // 5. Swap Available
                if (swapOnly) {
                    list = list.filter { it.isSwapAllowed }
                }

                list
            }
            _searchResults.value = filtered
        }
    }
}
