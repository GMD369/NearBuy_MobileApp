package com.nearbuy.app.data.mock

import com.nearbuy.app.data.model.Category
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.model.User

object MockData {

    val users = listOf(
        User("1", "Ali Ahmed", 4.8f, "https://picsum.photos/id/1/200/200"),
        User("2", "Sara Khan", 4.5f, "https://picsum.photos/id/2/200/200"),
        User("3", "Usman Sheikh", 4.2f, "https://picsum.photos/id/3/200/200")
    )

    val categories = listOf(
        Category("1", "Mobiles", android.R.drawable.ic_menu_call),
        Category("2", "Vehicles", android.R.drawable.ic_menu_directions),
        Category("3", "Electronics", android.R.drawable.ic_menu_camera),
        Category("4", "Furniture", android.R.drawable.ic_menu_sort_by_size),
        Category("5", "Property", android.R.drawable.ic_menu_myplaces)
    )

    val listings = listOf(
        Listing(
            "1", "iPhone 12 PTA Approved", 120000.0, 
            listOf("https://picsum.photos/id/160/800/600", "https://picsum.photos/id/161/800/600", "https://picsum.photos/id/162/800/600"),
            "Selling my iPhone 12 in pristine condition. PTA Approved, 128GB variant. No repairs, all original parts. Battery health 88%. Comes with original box and cable.",
            "Karachi", "Mobiles", "Used - Like New", true, users[0], System.currentTimeMillis()
        ),
        Listing(
            "2", "Honda CG 125 2023", 155000.0, 
            listOf("https://picsum.photos/id/146/800/600", "https://picsum.photos/id/147/800/600"),
            "Brand new condition Honda CG 125, 2023 model. Only 2000km driven. First owner, complete documents. Just like a zero-meter bike.",
            "Lahore", "Vehicles", "Used - Excellent", false, users[2], System.currentTimeMillis() - 86400000 * 2
        ),
        Listing(
            "3", "Samsung LED TV 42 inch", 45000.0, 
            listOf("https://picsum.photos/id/250/800/600", "https://picsum.photos/id/251/800/600"),
            "Samsung 42 inch Smart LED TV. 4K resolution, HDR support. Built-in Netflix and YouTube. Working perfectly fine, no screen issues.",
            "Islamabad", "Electronics", "New", true, users[1], System.currentTimeMillis()
        ),
        Listing(
            "4", "Wooden Sofa Set (6 Seater)", 35000.0, 
            listOf("https://picsum.photos/id/20/800/600", "https://picsum.photos/id/21/800/600"),
            "Solid wood 6-seater sofa set. Very comfortable cushions, recently dry cleaned. Selling because moving to another city.",
            "Faisalabad", "Furniture", "Used", false, users[0], System.currentTimeMillis() - 86400000 * 5
        ),
        Listing(
            "5", "5 Marla Plot in DHA Phase 6", 8500000.0, 
            listOf("https://picsum.photos/id/122/800/600"),
            "Prime location 5 marla plot available in DHA Phase 6. All dues clear, possession ready. Best for investment or building your dream home.",
            "Lahore", "Property", "N/A", false, users[2], System.currentTimeMillis() - 86400000 * 10
        )
    )
}
