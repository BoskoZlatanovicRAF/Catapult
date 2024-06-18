package raf.rs.rma_projekat.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _loginState = MutableStateFlow(ProfileData.EMPTY)
    val loginState: StateFlow<ProfileData> = _loginState

    init {
        viewModelScope.launch {
            profileDataStore.data.collect { profileData ->
                _loginState.value = profileData
            }
        }
    }

    fun updateProfileData(profileData: ProfileData) {
        viewModelScope.launch {
            profileDataStore.updateProfileData(profileData)
            _loginState.value = profileData
        }
    }

    suspend fun updateNickname(nickname: String): ProfileData {
        return profileDataStore.updateNickname(nickname).also {
            _loginState.value = it
        }
    }

    suspend fun clearProfileData() {
        profileDataStore.clearProfileData()
        _loginState.value = ProfileData.EMPTY
    }
}
