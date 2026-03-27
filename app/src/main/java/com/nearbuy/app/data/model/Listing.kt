package com.nearbuy.app.data.model

data class Listing(
    val id: String,
    val title: String,
    val price: Double,
    val images: List<String>,
    val description: String,
    val location: String,
    val category: String,
    val condition: String,
    val isSwapAvailable: Boolean,
    val user: User,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Keep this for backward compatibility or easy access to first image
    val imageUrl: String get() = images.firstOrNull() ?: ""
}
