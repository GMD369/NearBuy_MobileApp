package com.nearbuy.app.data.model

import androidx.annotation.DrawableRes

data class Category(
    val id: String,
    val name: String,
    @DrawableRes val icon: Int
)
