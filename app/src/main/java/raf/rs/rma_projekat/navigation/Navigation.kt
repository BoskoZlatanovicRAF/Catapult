package raf.rs.rma_projekat.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.runBlocking
import raf.rs.rma_projekat.catbreed.details.breedDetails
import raf.rs.rma_projekat.catbreed.gallery.photoGallery

import raf.rs.rma_projekat.catbreed.list.catbreeds
import raf.rs.rma_projekat.leaderboard.leaderBoard
import raf.rs.rma_projekat.navigation_bar.BottomNavigationBar
import raf.rs.rma_projekat.screens.Screen
import raf.rs.rma_projekat.quiz.quiz
import raf.rs.rma_projekat.user.LoginScreen
import raf.rs.rma_projekat.user.ProfileData
import raf.rs.rma_projekat.user.ProfileDataStore
import raf.rs.rma_projekat.user.profileScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(profileDataStore: ProfileDataStore) {
    val navController = rememberNavController()
    val profileData by profileDataStore.dataFlow.collectAsState(initial = ProfileData.EMPTY)

    val startDestination = if (profileData.nickname.isEmpty()) "login" else Screen.CatBreeds.route

    Scaffold(
        bottomBar = {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route
            if (currentRoute != Screen.Quiz.route && startDestination != "login") {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) {
        NavHost(navController, startDestination = startDestination, Modifier.padding(bottom = 70.dp)) {
            composable("login") {
                LoginScreen { profileData ->
                    navController.navigate(Screen.CatBreeds.route) {
                        popUpTo("login") { inclusive = true }
                    }
                    runBlocking {
                        profileDataStore.updateProfileData(profileData)
                    }
                }
            }
            catbreeds(
                route = Screen.CatBreeds.route,
                onCatBreedClick = {
                    navController.navigate(route = "${Screen.CatBreeds.route}/$it")
                }
            )

            breedDetails(
                route = "${Screen.CatBreeds.route}/{breedId}",
                arguments = listOf(
                    navArgument(name = "breedId") {
                        nullable = false
                        type = NavType.StringType
                    }
                ),
                onClose = {
                    navController.navigateUp()
                },
                onImageClick = { breedId ->
                    navController.navigate(route = "gallery/$breedId")
                }
            )

            photoGallery(
                route = "gallery/{breedId}",
                arguments = listOf(
                    navArgument(name = "breedId") {
                        nullable = false
                        type = NavType.StringType
                    }
                )
            )  {
                navController.navigateUp()
            }

            quiz(route = Screen.Quiz.route, navController)
            leaderBoard(route = Screen.LeaderBoard.route)
            profileScreen(route = Screen.Profile.route, profileDataStore)

        }
    }
}


inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breedId")) { "Breed id not found in saved state handle" }
