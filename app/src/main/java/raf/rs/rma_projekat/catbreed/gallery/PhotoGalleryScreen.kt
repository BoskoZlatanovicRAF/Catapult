package raf.rs.rma_projekat.catbreed.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

fun NavGraphBuilder.photoGallery(
    route: String,
    arguments: List<NamedNavArgument>,
    onClose: () -> Unit
) = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->
    val viewModel = hiltViewModel<PhotoGalleryViewModel>(navBackStackEntry)
    val state = viewModel.state.collectAsState()

    PhotoGallery(
        state = state.value,
        onClose = onClose,
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGallery(
    state: PhotoGalleryState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Failed to load images. Error in class PhotoGalleryScreen. Error: ${state.error}")
        }
    } else {
        var showPager by remember { mutableStateOf(false) }
        var initialPage by remember { mutableStateOf(0) }

        if (showPager) {
            val pagerState = rememberPagerState(pageCount = { state.images.size }, initialPage = initialPage)
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(state = pagerState) { page ->
                    val image = state.images[page]
                    AsyncImage(
                        model = image.url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(onClick = { showPager = false }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to gallery"
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }

                items(state.images) { image ->
                    Image(
                        painter = rememberAsyncImagePainter(image.url),
                        contentDescription = "Breed Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)) // Rounded corners
                            .border(2.dp, MaterialTheme.colorScheme.onBackground) // Border
                            .shadow(5.dp)
                            .clickable {
                                initialPage = state.images.indexOf(image)
                                showPager = true
                            }, // Shadow
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
