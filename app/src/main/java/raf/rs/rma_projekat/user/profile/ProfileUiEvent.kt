package raf.rs.rma_projekat.user.profile

sealed class ProfileUiEvent {
    object EditNickname : ProfileUiEvent()
    object EditFullName : ProfileUiEvent()
    object EditEmail : ProfileUiEvent()
    data class UpdateNickname(val nickname: String) : ProfileUiEvent()
    data class UpdateFullName(val fullName: String) : ProfileUiEvent()
    data class UpdateEmail(val email: String) : ProfileUiEvent()
    object SaveNickname : ProfileUiEvent()
    object SaveFullName : ProfileUiEvent()
    object SaveEmail : ProfileUiEvent()
    object CancelEdit : ProfileUiEvent()
    object Logout : ProfileUiEvent()
    data class Login(val profileData: ProfileData) : ProfileUiEvent()
}

