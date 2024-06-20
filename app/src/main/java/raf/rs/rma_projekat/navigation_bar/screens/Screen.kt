package raf.rs.rma_projekat.navigation_bar.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    object Profile : Screen("profile", Icons.Outlined.Person, Icons.Rounded.Person)
    object Quiz : Screen("quiz", Icons.Outlined.Quiz, Icons.Rounded.Quiz)
    object CatBreeds : Screen("catbreeds", Icons.Outlined.Pets ,Icons.Rounded.Pets)
    object LeaderBoard : Screen("leaderboard", Icons.Outlined.Leaderboard, Icons.Rounded.Leaderboard)
}

