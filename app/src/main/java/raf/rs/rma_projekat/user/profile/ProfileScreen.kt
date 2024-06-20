@file:Suppress("DEPRECATION")

package raf.rs.rma_projekat.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.runBlocking
import raf.rs.rma_projekat.core.theme.poppinsBold
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.leaderboard.UserHead
import raf.rs.rma_projekat.navigation_bar.screens.Screen

fun NavGraphBuilder.profileScreen(
    route: String,
    navController: NavController
) = composable(
    route = route
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileState by profileViewModel.state.collectAsState()

    ProfileScreen(
        profileState = profileState,
        eventPublisher = { event ->
            profileViewModel.setEvent(event)
            if (event is ProfileUiEvent.Logout) {
                navController.navigate("login") {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            }
        }
    )
}



@Composable
fun ProfileScreen(profileState: ProfileState, eventPublisher: (ProfileUiEvent) -> Unit) {
    val isNicknameValid = remember(profileState.newNickname) { profileState.newNickname.matches(Regex("^[a-zA-Z0-9_]+$")) }
    val isFullNameValid = remember(profileState.newFullName) { profileState.newFullName.isNotBlank() }
    val isEmailValid = remember(profileState.newEmail) { profileState.newEmail.matches(Regex("^[a-z]+@[a-z]+\\.com$")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        UserHead(nickname = profileState.profileData.fullName, size = 80.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = profileState.profileData.fullName, style = poppinsBold, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(32.dp))
        ProfileTextField(
            label = "Nickname",
            value = profileState.newNickname,
            isEditing = profileState.isEditingNickname,
            onValueChange = { eventPublisher(ProfileUiEvent.UpdateNickname(it)) },
            onEdit = { eventPublisher(ProfileUiEvent.EditNickname) },
            onSave = { if (isNicknameValid) eventPublisher(ProfileUiEvent.SaveNickname) },
            onCancel = { eventPublisher(ProfileUiEvent.CancelEdit) },
            isError = !isNicknameValid
        )

        ProfileTextField(
            label = "Full Name",
            value = profileState.newFullName,
            isEditing = profileState.isEditingFullName,
            onValueChange = { eventPublisher(ProfileUiEvent.UpdateFullName(it)) },
            onEdit = { eventPublisher(ProfileUiEvent.EditFullName) },
            onSave = { if (isFullNameValid) eventPublisher(ProfileUiEvent.SaveFullName) },
            onCancel = { eventPublisher(ProfileUiEvent.CancelEdit) },
            isError = !isFullNameValid
        )

        ProfileTextField(
            label = "Email",
            value = profileState.newEmail,
            isEditing = profileState.isEditingEmail,
            onValueChange = { eventPublisher(ProfileUiEvent.UpdateEmail(it)) },
            onEdit = { eventPublisher(ProfileUiEvent.EditEmail) },
            onSave = { if (isEmailValid) eventPublisher(ProfileUiEvent.SaveEmail) },
            onCancel = { eventPublisher(ProfileUiEvent.CancelEdit) },
            isError = !isEmailValid
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { eventPublisher(ProfileUiEvent.Logout) },
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout Icon")
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isError: Boolean
) {
    Column {
        Text(label, style = poppinsMedium, modifier = Modifier.padding(8.dp))
        if (isEditing) {
            TextField(
                colors = TextFieldDefaults.textFieldColors(containerColor = MaterialTheme.colorScheme.surface),
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                trailingIcon = {
                    Row {
                        IconButton(onClick = onSave) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                        }
                        IconButton(onClick = onCancel) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                        }
                    }
                }
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = value, style = poppinsMedium)
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

