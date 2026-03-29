package com.nearbuy.app.data.repository

import com.nearbuy.app.data.local.LocalStorageManager
import com.nearbuy.app.data.model.SwapProposal
import com.nearbuy.app.data.model.SwapStatus
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.io.File

class SwapProposalRepository(private val storage: LocalStorageManager, private val context: android.content.Context) {
    private val gson = Gson()
    private val proposalsFile = File(context.filesDir, "nearbuy_data/proposals.json")

    private fun readProposals(): List<SwapProposal> {
        if (!proposalsFile.exists()) return emptyList()
        return try {
            val type = object : TypeToken<List<SwapProposal>>() {}.type
            gson.fromJson(proposalsFile.readText(), type) ?: emptyList()
        } catch (e: Exception) { emptyList() }
    }

    private fun writeProposals(proposals: List<SwapProposal>) {
        proposalsFile.parentFile?.mkdirs()
        proposalsFile.writeText(gson.toJson(proposals))
    }

    fun getProposalsReceived(userId: String): List<SwapProposal> =
        readProposals().filter { it.proposedTo.id == userId }

    fun getProposalsSent(userId: String): List<SwapProposal> =
        readProposals().filter { it.proposedBy.id == userId }

    fun addProposal(proposal: SwapProposal) {
        val proposals = readProposals().toMutableList()
        proposals.add(0, proposal)
        writeProposals(proposals)
    }
}
