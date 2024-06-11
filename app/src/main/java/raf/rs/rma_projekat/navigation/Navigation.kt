package raf.rs.rma_projekat.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import raf.rs.rma_projekat.catbreed.details.breedDetails
import raf.rs.rma_projekat.catbreed.gallery.photoGallery

import raf.rs.rma_projekat.catbreed.list.catbreeds

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "catbreeds"
    ) {
        catbreeds(
            route = "catbreeds",
            onCatBreedClick = {
                navController.navigate(route = "catbreeds/$it")
            }
        )

        breedDetails(
            route = "catbreeds/{breedId}",
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

//        photoViewer(
//            route = "photoViewer/{breedId}/{imageId}",
//            arguments = listOf(
//                navArgument(name = "breedId") {
//                    nullable = false
//                    type = NavType.StringType
//                },
//                navArgument(name = "imageId") {
//                    nullable = false
//                    type = NavType.StringType
//                }
//            )
//        )
    }
}

inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breedId")) { "Breed id not found in saved state handle" }
