package raf.rs.rma_projekat.user.profile

data class ProfileState(
    val profileData: ProfileData = ProfileData.EMPTY,
    val isEditingNickname: Boolean = false,
    val isEditingFullName: Boolean = false,
    val isEditingEmail: Boolean = false,
    val newNickname: String = "",
    val newFullName: String = "",
    val newEmail: String = "",
    val isLoggedIn: Boolean = false
)
