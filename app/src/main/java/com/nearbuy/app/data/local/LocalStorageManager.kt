package com.nearbuy.app.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nearbuy.app.data.model.Listing
import com.nearbuy.app.data.model.User
import java.io.File

class LocalStorageManager(private val context: Context) {

    private val gson = Gson()
    private val dataDir: File by lazy {
        File(context.filesDir, "nearbuy_data").also { it.mkdirs() }
    }

    // ── Users ──────────────────────────────────────────────────────

    private val usersFile get() = File(dataDir, "users.json")

    fun readUsers(): List<User> {
        if (!usersFile.exists()) return emptyList()
        return try {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(usersFile.readText(), type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun writeUsers(users: List<User>) {
        usersFile.writeText(gson.toJson(users))
    }

    // ── Listings ───────────────────────────────────────────────────

    private val listingsFile get() = File(dataDir, "listings.json")

    fun readListings(): List<Listing> {
        if (!listingsFile.exists()) return emptyList()
        return try {
            val type = object : TypeToken<List<Listing>>() {}.type
            gson.fromJson(listingsFile.readText(), type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    fun writeListings(listings: List<Listing>) {
        listingsFile.writeText(gson.toJson(listings))
    }

    // ── Image Storage ──────────────────────────────────────────────

    fun getProfileImagesDir(): File =
        File(dataDir, "profile_images").also { it.mkdirs() }

    fun getListingImagesDir(): File =
        File(dataDir, "listing_images").also { it.mkdirs() }

    fun copyImageToStorage(sourcePath: String, targetDir: File, fileName: String): String {
        val source = File(sourcePath)
        val target = File(targetDir, fileName)
        source.copyTo(target, overwrite = true)
        return target.absolutePath
    }

    // ── Favorites ──────────────────────────────────────────────────

    private fun favPrefs() =
        context.getSharedPreferences("nearbuy_favorites", Context.MODE_PRIVATE)

    fun readFavorites(userId: String): Set<String> {
        val raw = favPrefs().getString("fav_$userId", "") ?: ""
        return if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }

    fun writeFavorites(userId: String, ids: Set<String>) {
        favPrefs().edit().putString("fav_$userId", ids.joinToString(",")).apply()
    }
}
