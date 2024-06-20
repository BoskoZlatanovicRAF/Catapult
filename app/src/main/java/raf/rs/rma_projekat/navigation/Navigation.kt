package raf.rs.rma_projekat.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import raf.rs.rma_projekat.catbreed.details.breedDetails
import raf.rs.rma_projekat.catbreed.gallery.photoGallery

import raf.rs.rma_projekat.catbreed.list.catbreeds
import raf.rs.rma_projekat.leaderboard.leaderBoard
import raf.rs.rma_projekat.navigation_bar.BottomNavigationBar
import raf.rs.rma_projekat.navigation_bar.screens.Screen
import raf.rs.rma_projekat.quiz.quiz
import raf.rs.rma_projekat.user.login.login
import raf.rs.rma_projekat.user.profile.ProfileData
import raf.rs.rma_projekat.user.profile.ProfileDataStore
import raf.rs.rma_projekat.user.profile.ProfileViewModel
import raf.rs.rma_projekat.user.profile.profileScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RestrictedApi")
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navigationViewModel = hiltViewModel<NavigationViewModel>()
    val navigationState by navigationViewModel.navigationState.collectAsState()


    Scaffold(
        bottomBar = {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route
            if (currentRoute != Screen.Quiz.route && currentRoute != "login") {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) {
        NavHost(navController, startDestination = "loading", Modifier.padding(bottom = 70.dp)) {
            composable("loading") {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
                LaunchedEffect(navigationState) {
                    when (navigationState) {
                        is NavigationState.Login -> navController.navigate("login") {
                            popUpTo("loading") { inclusive = true }
                        }
                        is NavigationState.Main -> navController.navigate(Screen.CatBreeds.route) {
                            popUpTo("loading") { inclusive = true }
                        }
                        else -> { /* TODO */ }
                    }
                }
            }
            login(
                route = "login",
                onLoginClick = {
                    navController.navigate(Screen.CatBreeds.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )

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
            profileScreen(route = Screen.Profile.route, navController = navController)
        }
    }
}



inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breedId")) { "Breed id not found in saved state handle" }
