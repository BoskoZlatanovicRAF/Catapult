package raf.rs.rma_projekat.user.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rs.rma_projekat.navigation_bar.screens.Screen
import raf.rs.rma_projekat.user.profile.ProfileData
import raf.rs.rma_projekat.user.profile.ProfileState
import raf.rs.rma_projekat.user.profile.ProfileUiEvent
import raf.rs.rma_projekat.user.profile.ProfileViewModel


fun NavGraphBuilder.login(
    route: String,
    onLoginClick: () -> Unit
) = composable(
    route = route
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileState by profileViewModel.state.collectAsState()

    LoginScreen(
        profileState = profileState,
        eventPublisher = { event ->
            profileViewModel.setEvent(event)
        },
        onLoginClick = onLoginClick
    )
}

@Composable
fun LoginScreen(
    profileState: ProfileState,
    eventPublisher: (ProfileUiEvent) -> Unit,
    onLoginClick: () -> Unit
) {
    var nickname by remember { mutableStateOf(profileState.profileData.nickname) }
    var fullName by remember { mutableStateOf(profileState.profileData.fullName) }
    var email by remember { mutableStateOf(profileState.profileData.email) }

    val isFormValid = nickname.matches(Regex("^[a-zA-Z0-9_]+$")) &&
                  fullName.isNotBlank() &&
                  email.isNotBlank() &&
                  email.matches(Regex("^[a-z]+@[a-z]+\\.com$"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Nickname") },
            isError = !nickname.matches(Regex("^[a-zA-Z0-9_]+$")) && nickname.isNotEmpty(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { if (isFormValid) {
                eventPublisher(ProfileUiEvent.Login(ProfileData(nickname, fullName, email)))
                onLoginClick()
            } },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text("Login")
        }
    }
}
