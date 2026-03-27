package com.nearbuy.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.repository.ListingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val repository = ListingRepository()

    private val _listings = MutableLiveData<List<Listing>>()
    val listings: LiveData<List<Listing>> get() = _listings

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _refreshSuccess = MutableLiveData<Boolean>()
    val refreshSuccess: LiveData<Boolean> get() = _refreshSuccess

    private var fullListCache: List<Listing> = emptyList()
    private var currentFilter: String = "All"
    private var currentQuery: String = ""

    init {
        loadListings()
    }

    fun loadListings(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (!isRefreshing) _isLoading.value = true
            _error.value = null
            
            try {
                // Simulate network call
                val result = withContext(Dispatchers.IO) {
                    repository.getAllListings()
                }
                fullListCache = result
                applyFilters()
                
                if (isRefreshing) {
                    _refreshSuccess.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(categoryName: String) {
        if (currentFilter == categoryName) return
        currentFilter = categoryName
        applyFilters()
    }

    fun search(query: String) {
        currentQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch(Dispatchers.Default) {
            var filtered = if (currentFilter == "All") {
                fullListCache
            } else {
                fullListCache.filter { it.category == currentFilter }
            }

            if (currentQuery.isNotBlank()) {
                filtered = filtered.filter { 
                    it.title.contains(currentQuery, ignoreCase = true) ||
                    it.description.contains(currentQuery, ignoreCase = true)
                }
            }

            withContext(Dispatchers.Main) {
                _listings.value = filtered
            }
        }
    }
    
    fun onRefreshHandled() {
        _refreshSuccess.value = false
    }
}
