package raf.rs.rma_projekat.navigation_bar

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.screens.Screen
import java.util.Locale

@Suppress("DEPRECATION")
@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = Color.DarkGray,
        modifier = Modifier.padding(top = 4.dp)
    ) {

        val screens = listOf(Screen.Profile, Screen.LeaderBoard, Screen.Quiz, Screen.CatBreeds)
        val catBreedRoutes = listOf("catbreeds", "catbreeds/{breedId}", "gallery/{breedId}")

        Log.d("BottomNavigationBar", "currentRoute: $currentRoute")

        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Log.d("BottomNavigationBar", screen.route)
                    if (currentRoute == screen.route ||
                        (screen.route == Screen.CatBreeds.route && catBreedRoutes.any { route -> currentRoute?.startsWith(route.split("/")[0]) == true })) {
                        Icon(screen.selectedIcon, contentDescription = null)
                    } else {
                        Icon(screen.icon, contentDescription = null)
                    }
                },
                label = { Text(text = screen.route.capitalize(Locale.ROOT), style = poppinsMedium) },
                selected = currentRoute == screen.route ||
                        (screen.route == Screen.CatBreeds.route && catBreedRoutes.any { route -> currentRoute?.startsWith(route.split("/")[0]) == true }),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo = navController.graph.startDestinationId
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.LightGray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color.LightGray
                )
            )
        }
    }
}

