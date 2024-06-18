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
import raf.rs.rma_projekat.user.profile.ProfileViewModel


fun NavGraphBuilder.login(
    route: String,
    navController: NavController
) = composable(
    route = route
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val loginState by profileViewModel.loginState.collectAsState()

    LoginScreen(
        onLogin = { profileData ->
            profileViewModel.updateProfileData(profileData)
            navController.navigate(Screen.CatBreeds.route) {
                popUpTo(route) { inclusive = true }
            }
        },
        loginState = loginState
    )
}

@Composable
fun LoginScreen(onLogin: (ProfileData) -> Unit, loginState: ProfileData) {
    var nickname by remember { mutableStateOf(loginState.nickname) }
    var fullName by remember { mutableStateOf(loginState.fullName) }
    var email by remember { mutableStateOf(loginState.email) }

    val isFormValid = nickname.matches(Regex("^[a-zA-Z0-9_]+$")) && fullName.isNotBlank() && email.isNotBlank()

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
            onClick = { if (isFormValid) onLogin(ProfileData(nickname, fullName, email)) },
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



