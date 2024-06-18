package raf.rs.rma_projekat.navigation

sealed class NavigationState {
    object Loading : NavigationState()
    object Login : NavigationState()
    object Main : NavigationState()
}