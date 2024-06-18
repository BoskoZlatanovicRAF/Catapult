package raf.rs.rma_projekat.user.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import kotlinx.coroutines.runBlocking
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.navigation_bar.screens.Screen

fun NavGraphBuilder.profileScreen(
    route: String,
    navController: NavController
) = composable(
    route = route
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileData by profileViewModel.loginState.collectAsState()

    ProfileScreen(
        profileData = profileData,
        onEditNickname = { newNickname ->
            runBlocking {
                profileViewModel.updateNickname(newNickname)
            }
        },
        onLogout = {
            runBlocking {
                profileViewModel.clearProfileData()
                navController.navigate("login") {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            }
        }
    )
}

@Composable
fun ProfileScreen(profileData: ProfileData, onEditNickname: (String) -> Unit, onLogout: () -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var newNickname by remember { mutableStateOf(profileData.nickname) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(text = "Full Name: ${profileData.fullName}", style = poppinsMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Email: ${profileData.email}", style = poppinsMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            TextField(
                value = newNickname,
                onValueChange = { newNickname = it },
                label = { Text("Nickname") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        onEditNickname(newNickname)
                        isEditing = false
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Save")
                }
                Button(onClick = { isEditing = false }) {
                    Text("Cancel")
                }
            }
        } else {
            Text(text = "Nickname: ${profileData.nickname}", style = poppinsMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { isEditing = true }) {
                Text("Edit Nickname")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}

