package raf.rs.rma_projekat.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import raf.rs.rma_projekat.catbreed.details.breedDetails
import raf.rs.rma_projekat.catbreed.gallery.photoGallery

import raf.rs.rma_projekat.catbreed.list.catbreeds
import raf.rs.rma_projekat.navigation_bar.BottomNavigationBar
import raf.rs.rma_projekat.navigation_bar.Screen
import raf.rs.rma_projekat.quiz.quiz


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route
            if(currentRoute != Screen.Quiz.route)
                BottomNavigationBar(navController, currentRoute)
        }
    ) {
        NavHost(navController, startDestination = Screen.CatBreeds.route, Modifier.padding(bottom = 70.dp)) {
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

            quiz(route = Screen.Quiz.route)
//            profileScreen(route = Screen.Profile.route)

        }
    }
}
//@Composable
//fun Navigation() {
//    val navController = rememberNavController()
//    NavHost(
//        navController = navController,
//        startDestination = "catbreeds"
//    ) {
//        catbreeds(
//            route = "catbreeds",
//            onCatBreedClick = {
//                navController.navigate(route = "catbreeds/$it")
//            }
//        )
//
//        breedDetails(
//            route = "catbreeds/{breedId}",
//            arguments = listOf(
//                navArgument(name = "breedId") {
//                    nullable = false
//                    type = NavType.StringType
//                }
//            ),
//            onClose = {
//                navController.navigateUp()
//            },
//            onImageClick = { breedId ->
//                navController.navigate(route = "gallery/$breedId")
//            }
//        )
//
//        photoGallery(
//            route = "gallery/{breedId}",
//            arguments = listOf(
//                navArgument(name = "breedId") {
//                    nullable = false
//                    type = NavType.StringType
//                }
//            )
//        )  {
//            navController.navigateUp()
//        }
//
//    }
//}

inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breedId")) { "Breed id not found in saved state handle" }
