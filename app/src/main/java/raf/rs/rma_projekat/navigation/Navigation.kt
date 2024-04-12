package raf.rs.rma_projekat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import raf.rs.rma_projekat.catbreed.details.breedDetails
import raf.rs.rma_projekat.catbreed.list.catbreeds

@Composable
fun Navigation() {
    //instanca navController-a
    val navController = rememberNavController()
    //definisanje NavHost-a sa startnom destinacijom "catbreeds", unutar koga se definise navGraph
    NavHost(
        navController = navController,
        startDestination = "catbreeds"
    ) {
        catbreeds(
            route = "catbreeds",
            onCatBreedClick =  {
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
            }
        )
    }
}