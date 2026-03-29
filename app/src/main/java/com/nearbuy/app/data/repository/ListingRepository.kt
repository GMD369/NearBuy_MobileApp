package com.nearbuy.app.data.repository

import com.nearbuy.app.data.local.LocalStorageManager
import com.nearbuy.app.data.model.Listing
import java.util.UUID

class ListingRepository(private val storage: LocalStorageManager) {

    fun getAllListings(): List<Listing> =
        storage.readListings().filter { it.isActive }

    fun getListingsByUser(userId: String): List<Listing> =
        storage.readListings().filter { it.sellerId == userId && it.isActive }

    fun getListingById(id: String): Listing? =
        storage.readListings().find { it.id == id }

    fun getListingsByCategory(category: String): List<Listing> =
        if (category.equals("All", ignoreCase = true)) getAllListings()
        else getAllListings().filter { it.category.equals(category, ignoreCase = true) }

    fun searchListings(query: String): List<Listing> {
        val q = query.lowercase()
        return getAllListings().filter {
            it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.location.lowercase().contains(q)
        }
    }

    fun addListing(listing: Listing): Listing {
        val withId = if (listing.id.isEmpty())
            listing.copy(id = UUID.randomUUID().toString()) else listing
        val listings = storage.readListings().toMutableList()
        listings.add(0, withId)
        storage.writeListings(listings)
        return withId
    }

    fun updateListing(listing: Listing) {
        val listings = storage.readListings().toMutableList()
        val idx = listings.indexOfFirst { it.id == listing.id }
        if (idx >= 0) { listings[idx] = listing; storage.writeListings(listings) }
    }

    fun deleteListing(id: String) {
        val listings = storage.readListings().toMutableList()
        val idx = listings.indexOfFirst { it.id == id }
        if (idx >= 0) {
            listings[idx] = listings[idx].copy(isActive = false)
            storage.writeListings(listings)
        }
    }

    fun seedSampleData(sellerId: String, sellerName: String) {
        if (getAllListings().isNotEmpty()) return
        val samples = listOf(
            Listing(UUID.randomUUID().toString(), sellerId, sellerName, "0300-1234567",
                "Samsung Galaxy S21", "Used for 6 months, excellent condition", 45000.0,
                "Electronics", "Like New", "Karachi"),
            Listing(UUID.randomUUID().toString(), sellerId, sellerName, "0300-1234567",
                "Wooden Dining Table", "Solid wood, seats 6 people", 18000.0,
                "Furniture", "Good", "Lahore"),
            Listing(UUID.randomUUID().toString(), sellerId, sellerName, "0300-1234567",
                "Mountain Bike", "Trek bicycle, barely used", 25000.0,
                "Sports", "Like New", "Islamabad"),
            Listing(UUID.randomUUID().toString(), sellerId, sellerName, "0300-1234567",
                "Introduction to Algorithms", "CS textbook, 3rd edition", 2500.0,
                "Books", "Good", "Karachi"),
            Listing(UUID.randomUUID().toString(), sellerId, sellerName, "0300-1234567",
                "Leather Sofa Set", "3+2 seater, brown leather", 35000.0,
                "Furniture", "Good", "Lahore"),
            Listing(UUID.randomUUID().toString(), sellerId, sellerName, "0300-1234567",
                "Kids Bicycle", "Small red bicycle for age 5-8", 4000.0,
                "Sports", "Good", "Rawalpindi", isSwapAllowed = true),
        )
        storage.writeListings(samples)
    }

    // ── Favorites ─────────────────────────────────────────────────

    fun toggleFavorite(userId: String, listingId: String): Boolean {
        val favs = storage.readFavorites(userId).toMutableSet()
        return if (favs.contains(listingId)) {
            favs.remove(listingId)
            storage.writeFavorites(userId, favs)
            false
        } else {
            favs.add(listingId)
            storage.writeFavorites(userId, favs)
            true
        }
    }

    fun getFavorites(userId: String): Set<String> = storage.readFavorites(userId)

    fun getFavoriteListings(userId: String): List<Listing> {
        val favIds = storage.readFavorites(userId)
        return getAllListings().filter { it.id in favIds }
    }

    companion object {
        val CATEGORIES = listOf(
            "All", "Electronics", "Furniture", "Vehicles",
            "Clothing", "Sports", "Books", "Home Appliances", "Other"
        )
    }
}
