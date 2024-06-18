package raf.rs.rma_projekat.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import raf.rs.rma_projekat.user.profile.ProfileData
import raf.rs.rma_projekat.user.profile.ProfileDataStore
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Loading)
    val navigationState: StateFlow<NavigationState> = _navigationState

    init {
        checkProfile()
    }

    private fun checkProfile() {
        viewModelScope.launch {
            profileDataStore.data.firstOrNull()?.let { profileData ->
                _navigationState.value = if (profileData.nickname.isEmpty()) {
                    NavigationState.Login
                } else {
                    NavigationState.Main
                }
            } ?: run {
                _navigationState.value = NavigationState.Login
            }
        }
    }

}

