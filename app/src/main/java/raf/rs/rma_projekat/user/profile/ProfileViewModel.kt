package raf.rs.rma_projekat.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    private val _event = MutableSharedFlow<ProfileUiEvent>()
    fun setEvent(event: ProfileUiEvent) = viewModelScope.launch { _event.emit(event) }

    init {
        handleEvents()
        setProfileData()
    }

    private fun handleEvents() {
        viewModelScope.launch {
            _event.collect { event ->
                when (event) {
                    is ProfileUiEvent.EditNickname -> setState { copy(isEditingNickname = true, newNickname = profileData.nickname) }
                    is ProfileUiEvent.EditFullName -> setState { copy(isEditingFullName = true, newFullName = profileData.fullName) }
                    is ProfileUiEvent.EditEmail -> setState { copy(isEditingEmail = true, newEmail = profileData.email) }
                    is ProfileUiEvent.UpdateNickname -> setState { copy(newNickname = event.nickname) }
                    is ProfileUiEvent.UpdateFullName -> setState { copy(newFullName = event.fullName) }
                    is ProfileUiEvent.UpdateEmail -> setState { copy(newEmail = event.email) }
                    ProfileUiEvent.SaveNickname -> updateNickname(state.value.newNickname)
                    ProfileUiEvent.SaveFullName -> updateFullName(state.value.newFullName)
                    ProfileUiEvent.SaveEmail -> updateEmail(state.value.newEmail)
                    ProfileUiEvent.CancelEdit -> setState {
                        copy(
                            isEditingNickname = false,
                            isEditingFullName = false,
                            isEditingEmail = false,
                            newNickname = profileData.nickname,
                            newFullName = profileData.fullName,
                            newEmail = profileData.email
                        )
                    }
                    ProfileUiEvent.Logout -> logout()
                    is ProfileUiEvent.Login -> login(event.profileData)
                }
            }
        }
    }

    private fun setProfileData() {
        viewModelScope.launch {
            profileDataStore.data.collect { profileData ->
                setState {
                    copy(
                        profileData = profileData,
                        newNickname = profileData.nickname,
                        newFullName = profileData.fullName,
                        newEmail = profileData.email
                    )
                }
            }
        }
    }

    private fun setState(reducer: ProfileState.() -> ProfileState) {
        _state.update(reducer)
    }

    private fun updateNickname(nickname: String) {
        viewModelScope.launch {
            val updatedProfileData = profileDataStore.updateNickname(nickname)
            setState { copy(profileData = updatedProfileData, isEditingNickname = false) }
        }
    }

    private fun updateFullName(fullName: String) {
        viewModelScope.launch {
            val updatedProfileData = profileDataStore.updateFullName(fullName)
            setState { copy(profileData = updatedProfileData, isEditingFullName = false) }
        }
    }

    private fun updateEmail(email: String) {
        viewModelScope.launch {
            val updatedProfileData = profileDataStore.updateEmail(email)
            setState { copy(profileData = updatedProfileData, isEditingEmail = false) }
        }
    }

    private fun login(profileData: ProfileData) {
        viewModelScope.launch {
            profileDataStore.updateProfileData(profileData)
            setState { copy(profileData = profileData, isLoggedIn = true) } // Update the state with isLoggedIn = true
        }
    }

    private fun logout() {
        viewModelScope.launch {
            profileDataStore.clearProfileData()
            setState { ProfileState() }
        }
    }
}



