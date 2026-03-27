package com.nearbuy.app.data.repository

import com.nearbuy.app.data.mock.MockData
import com.nearbuy.app.data.model.Listing

class ListingRepository {

    fun getAllListings(): List<Listing> {
        return MockData.listings
    }

    fun getListingsByCategory(categoryName: String): List<Listing> {
        return MockData.listings.filter { it.category == categoryName }
    }
}
