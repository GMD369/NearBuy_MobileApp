package com.nearbuy.app.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nearbuy_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID      = "user_id"
        private const val KEY_USER_EMAIL   = "user_email"
        private const val KEY_USER_NAME    = "user_name"
        private const val KEY_ONBOARDING   = "onboarding_done"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userId: String
        get() = prefs.getString(KEY_USER_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var isOnboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()

    fun login(userId: String, email: String, name: String) {
        isLoggedIn  = true
        this.userId = userId
        userEmail   = email
        userName    = name
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply()
    }
}
