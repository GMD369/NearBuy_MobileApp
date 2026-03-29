package com.nearbuy.app.ui.swap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.model.SwapProposal

class SwapViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as NearBuyApplication
    private val repo = app.listingRepository
    private val proposalRepo = app.swapProposalRepository

    private val _swapListings = MutableLiveData<List<Listing>>()
    val swapListings: LiveData<List<Listing>> = _swapListings

    private val _mySwapListings = MutableLiveData<List<Listing>>()
    val mySwapListings: LiveData<List<Listing>> = _mySwapListings

    private val _receivedProposals = MutableLiveData<List<SwapProposal>>()
    val receivedProposals: LiveData<List<SwapProposal>> = _receivedProposals

    private val _sentProposals = MutableLiveData<List<SwapProposal>>()
    val sentProposals: LiveData<List<SwapProposal>> = _sentProposals

    fun loadData() {
        val currentUserId = app.sessionManager.userId
        
        // Items available for swap from other users
        val othersSwapAllowed = repo.getAllListings().filter { it.isSwapAllowed && it.sellerId != currentUserId }
        _swapListings.value = othersSwapAllowed

        // User's own items open for swap
        val mySwaps = repo.getListingsByUser(currentUserId).filter { it.isSwapAllowed }
        _mySwapListings.value = mySwaps

        _receivedProposals.value = proposalRepo.getProposalsReceived(currentUserId)
        _sentProposals.value = proposalRepo.getProposalsSent(currentUserId)
    }
}
