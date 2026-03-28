package com.nearbuy.app.data.mock

import com.nearbuy.app.data.model.Category
import com.nearbuy.app.data.model.Listing

object MockData {

    val categories = listOf(
        Category("1", "Electronics", android.R.drawable.ic_menu_camera),
        Category("2", "Furniture", android.R.drawable.ic_menu_sort_by_size),
        Category("3", "Vehicles", android.R.drawable.ic_menu_directions),
        Category("4", "Clothing", android.R.drawable.ic_menu_gallery),
        Category("5", "Sports", android.R.drawable.ic_menu_compass),
        Category("6", "Books", android.R.drawable.ic_menu_edit),
        Category("7", "Home Appliances", android.R.drawable.ic_menu_preferences),
        Category("8", "Other", android.R.drawable.ic_menu_more)
    )

    // Sample listings used only for BrowseViewModel (mock/search preview)
    val listings = listOf(
        Listing(
            id = "1",
            sellerId = "sample",
            sellerName = "Ali Ahmed",
            title = "iPhone 12 PTA Approved",
            description = "Selling my iPhone 12 in pristine condition. PTA Approved, 128GB variant.",
            price = 120000.0,
            category = "Electronics",
            condition = "Used - Like New",
            location = "Karachi",
            imagePaths = emptyList(),
            isSwapAllowed = true
        ),
        Listing(
            id = "2",
            sellerId = "sample",
            sellerName = "Usman Sheikh",
            title = "Honda CG 125 2023",
            description = "Brand new condition Honda CG 125, 2023 model. Only 2000km driven.",
            price = 155000.0,
            category = "Vehicles",
            condition = "Used - Excellent",
            location = "Lahore",
            imagePaths = emptyList(),
            isSwapAllowed = false
        ),
        Listing(
            id = "3",
            sellerId = "sample",
            sellerName = "Sara Khan",
            title = "Samsung LED TV 42 inch",
            description = "Samsung 42 inch Smart LED TV. 4K resolution, HDR support.",
            price = 45000.0,
            category = "Electronics",
            condition = "New",
            location = "Islamabad",
            imagePaths = emptyList(),
            isSwapAllowed = true
        ),
        Listing(
            id = "4",
            sellerId = "sample",
            sellerName = "Ali Ahmed",
            title = "Wooden Sofa Set (6 Seater)",
            description = "Solid wood 6-seater sofa set. Very comfortable cushions.",
            price = 35000.0,
            category = "Furniture",
            condition = "Used",
            location = "Faisalabad",
            imagePaths = emptyList(),
            isSwapAllowed = false
        ),
        Listing(
            id = "5",
            sellerId = "sample",
            sellerName = "Usman Sheikh",
            title = "Mountain Bike",
            description = "Trek bicycle, barely used. Great for trails.",
            price = 25000.0,
            category = "Sports",
            condition = "Like New",
            location = "Islamabad",
            imagePaths = emptyList(),
            isSwapAllowed = true
        )
    )
}
