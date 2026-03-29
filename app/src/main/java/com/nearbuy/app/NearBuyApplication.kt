package com.nearbuy.app

import android.app.Application
import com.nearbuy.app.data.local.LocalStorageManager
import com.nearbuy.app.data.local.SessionManager
import com.nearbuy.app.data.repository.ListingRepository
import com.nearbuy.app.data.repository.UserRepository
import com.nearbuy.app.data.repository.SwapProposalRepository

class NearBuyApplication : Application() {

    lateinit var sessionManager: SessionManager
    lateinit var localStorageManager: LocalStorageManager
    lateinit var userRepository: UserRepository
    lateinit var listingRepository: ListingRepository
    lateinit var swapProposalRepository: SwapProposalRepository

    override fun onCreate() {
        super.onCreate()
        sessionManager          = SessionManager(this)
        localStorageManager     = LocalStorageManager(this)
        userRepository          = UserRepository(localStorageManager)
        listingRepository       = ListingRepository(localStorageManager)
        swapProposalRepository  = SwapProposalRepository(localStorageManager, this)
    }
}
