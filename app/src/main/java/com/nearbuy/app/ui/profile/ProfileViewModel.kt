package com.nearbuy.app.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.model.User

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val app      = application as NearBuyApplication
    private val userRepo = app.userRepository
    private val listRepo = app.listingRepository
    private val session  = app.sessionManager

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _userListings = MutableLiveData<List<Listing>>()
    val userListings: LiveData<List<Listing>> = _userListings

    private val _savedListings = MutableLiveData<List<Listing>>()
    val savedListings: LiveData<List<Listing>> = _savedListings

    private val _favoriteIds = MutableLiveData<Set<String>>()
    val favoriteIds: LiveData<Set<String>> = _favoriteIds

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (!session.isLoggedIn) {
            _user.value = null
            _userListings.value = emptyList()
            _savedListings.value = emptyList()
            _favoriteIds.value = emptySet()
            return
        }
        val userId = session.userId
        val u = userRepo.getUserById(userId)
        _user.value = u
        _userListings.value = if (u != null) listRepo.getListingsByUser(u.id) else emptyList()
        _savedListings.value = listRepo.getFavoriteListings(userId)
        _favoriteIds.value = listRepo.getFavorites(userId)
    }

    fun toggleFavorite(listingId: String) {
        if (!session.isLoggedIn) return
        listRepo.toggleFavorite(session.userId, listingId)
        loadProfile()
    }

    fun updateProfileImage(imagePath: String) {
        if (!session.isLoggedIn) return
        userRepo.updateProfileImage(session.userId, imagePath)
        loadProfile()
    }

    fun updateProfile(name: String, phone: String, location: String, bio: String) {
        val userId = session.userId
        if (userId.isBlank()) return

        val current = _user.value ?: userRepo.getUserById(userId) ?: return
        val updated = current.copy(name = name, phone = phone, location = location, bio = bio)

        userRepo.updateUser(updated)
        session.userName = name
        loadProfile()
    }

    fun deleteListing(listingId: String) {
        listRepo.deleteListing(listingId)
        loadProfile()
    }
}
