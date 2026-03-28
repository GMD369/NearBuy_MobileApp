package com.nearbuy.app.ui.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.data.model.Listing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseViewModel : ViewModel() {

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
        _searchResults.value = MockData.listings
        _activeFilters.value = emptyList()
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
            if (category != null && category != "All Categories") filters.add(category!!)
            if (condition != null) filters.add(condition!!)
            if (swapOnly) filters.add("Swap Available")
            if (minPrice != null && maxPrice != null && (minPrice!! > 0 || maxPrice!! < 1000000)) {
                filters.add("Price: Rs.${minPrice?.toInt()} - Rs.${maxPrice?.toInt()}")
            }
            _activeFilters.value = filters

            val filtered = withContext(Dispatchers.Default) {
                var list = MockData.listings

                // 1. Search Query
                if (currentQuery.isNotBlank()) {
                    list = list.filter { 
                        it.title.contains(currentQuery, ignoreCase = true) || 
                        it.description.contains(currentQuery, ignoreCase = true) ||
                        it.category.contains(currentQuery, ignoreCase = true)
                    }
                }

                // 2. Price Range
                minPrice?.let { min ->
                    list = list.filter { it.price >= min }
                }
                maxPrice?.let { max ->
                    list = list.filter { it.price <= max }
                }

                // 3. Category
                if (!category.isNullOrEmpty() && category != "All Categories") {
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
