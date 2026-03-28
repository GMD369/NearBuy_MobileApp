package com.nearbuy.app.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val phone: String = "",
    val location: String = "",
    val bio: String = "",
    val profileImagePath: String = "",
    val rating: Float = 0f,
    val listingsCount: Int = 0,
    val joinedAt: Long = System.currentTimeMillis()
)
