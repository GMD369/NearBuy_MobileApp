package com.nearbuy.app.data.model

data class SwapProposal(
    val id: String,
    val offeredItem: Listing,
    val requestedItem: Listing,
    val additionalCash: Double?,
    val status: SwapStatus,
    val proposedBy: User,
    val proposedTo: User
)

enum class SwapStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    COMPLETED
}
