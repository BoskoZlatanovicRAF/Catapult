package raf.rs.rma_projekat.catbreed.gallery.viewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import raf.rs.rma_projekat.catbreed.gallery.PhotoGalleryViewModel

fun NavGraphBuilder.photoViewer(
    route: String,
    arguments: List<NamedNavArgument>
) = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->
    val breedId = navBackStackEntry.arguments?.getString("breedId") ?: return@composable
    val imageId = navBackStackEntry.arguments?.getString("imageId") ?: return@composable
    val viewModel: PhotoGalleryViewModel = hiltViewModel(navBackStackEntry)
    PhotoViewerScreen(breedId = breedId, imageId = imageId, viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoViewerScreen(
    breedId: String,
    imageId: String,
    viewModel: PhotoGalleryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val initialPage = viewModel.getImageIndex(imageId)
    val pagerState = rememberPagerState(pageCount = { state.images.size }, initialPage = initialPage)

    HorizontalPager(
        state = pagerState
    ) { page ->
        val image = state.images[page]
        AsyncImage(
            model = image.url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}