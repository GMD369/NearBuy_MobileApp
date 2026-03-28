package com.nearbuy.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nearbuy.app.NearBuyApplication
import com.nearbuy.app.data.model.User

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val app      = application as NearBuyApplication
    private val userRepo = app.userRepository
    private val session  = app.sessionManager

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    init {
        if (session.isLoggedIn) {
            _currentUser.value = userRepo.getUserById(session.userId)
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank())    { _authResult.value = AuthResult.Error("Email is required"); return }
        if (password.isBlank()) { _authResult.value = AuthResult.Error("Password is required"); return }
        val user = userRepo.login(email.trim(), password)
        if (user != null) {
            session.login(user.id, user.email, user.name)
            _currentUser.value = user
            _authResult.value  = AuthResult.Success(user)
        } else {
            _authResult.value = AuthResult.Error("Invalid email or password")
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String, phone: String = "") {
        if (name.isBlank())  { _authResult.value = AuthResult.Error("Name is required"); return }
        if (email.isBlank()) { _authResult.value = AuthResult.Error("Email is required"); return }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authResult.value = AuthResult.Error("Enter a valid email"); return
        }
        if (password.length < 6) { _authResult.value = AuthResult.Error("Password must be at least 6 characters"); return }
        if (password != confirmPassword) { _authResult.value = AuthResult.Error("Passwords do not match"); return }

        val user = userRepo.register(name.trim(), email.trim(), password, phone.trim())
        if (user != null) {
            session.login(user.id, user.email, user.name)
            _currentUser.value = user
            _authResult.value = AuthResult.Success(user)
        } else {
            _authResult.value = AuthResult.Error("An account with this email already exists")
        }
    }

    fun logout() {
        session.logout()
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean = session.isLoggedIn

    fun refreshCurrentUser() {
        if (session.isLoggedIn) {
            _currentUser.value = userRepo.getUserById(session.userId)
        }
    }
}
