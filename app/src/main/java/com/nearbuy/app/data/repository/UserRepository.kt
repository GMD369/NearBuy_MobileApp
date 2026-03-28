package com.nearbuy.app.data.repository

import com.nearbuy.app.data.local.LocalStorageManager
import com.nearbuy.app.data.model.User
import java.security.MessageDigest
import java.util.UUID

class UserRepository(private val storage: LocalStorageManager) {

    fun register(name: String, email: String, password: String, phone: String = ""): User? {
        val users = storage.readUsers().toMutableList()
        if (users.any { it.email.equals(email, ignoreCase = true) }) return null
        val user = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email.lowercase(),
            passwordHash = hashPassword(password),
            phone = phone,
            joinedAt = System.currentTimeMillis()
        )
        users.add(user)
        storage.writeUsers(users)
        return user
    }

    fun login(email: String, password: String): User? {
        val hash = hashPassword(password)
        return storage.readUsers().find {
            it.email.equals(email, ignoreCase = true) && it.passwordHash == hash
        }
    }

    fun getUserById(id: String): User? =
        storage.readUsers().find { it.id == id }

    fun updateUser(updated: User) {
        val users = storage.readUsers().toMutableList()
        val idx = users.indexOfFirst { it.id == updated.id }
        if (idx >= 0) { users[idx] = updated; storage.writeUsers(users) }
    }

    fun updateProfileImage(userId: String, imagePath: String) {
        val users = storage.readUsers().toMutableList()
        val idx = users.indexOfFirst { it.id == userId }
        if (idx >= 0) {
            users[idx] = users[idx].copy(profileImagePath = imagePath)
            storage.writeUsers(users)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
