package raf.rs.rma_projekat.user.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileData(
    val nickname: String,
    val fullName: String,
    val email: String,
) {
    companion object {
        val EMPTY = ProfileData(nickname = "", fullName = "", email = "")
    }
}