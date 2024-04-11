package raf.rs.rma_projekat.catbreed.details

import android.app.LauncherActivity
import android.content.Intent
import android.net.Uri
import android.widget.ProgressBar
import android.widget.RatingBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter

fun NavGraphBuilder.breedDetails(
    route: String,
    arguments: List<NamedNavArgument>,
    onClose: () -> Unit
) = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->
    val breedId = navBackStackEntry.arguments?.getString("breedId") ?: throw IllegalArgumentException("Missing cat breed id")

    val detailsViewModel = viewModel<DetailsViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailsViewModel(breedId = breedId) as T
            }
        }
    )

    val state = detailsViewModel.state.collectAsState()

    BreedDetails(
        state = state.value,
        eventPublisher = {
            detailsViewModel.setEvent(it)
        },
        onClose = onClose,
        modifier = Modifier.fillMaxSize(),
        viewModel = detailsViewModel
    )

}
@Composable
fun BreedDetails(
    state: DetailsState,
    eventPublisher: (DetailsUiEvent) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel
) {
    if (state.loading) {
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    } else if (state.error.isNotEmpty()) {
        Text(text = "Error: ${state.error}")
    } else {
        state.breedsDetail?.let { breedDetails ->
            Card(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                Column (
                    modifier = Modifier.padding(8.dp)
                ){
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }

                    state.breedImage?.url?.let { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Breed Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)) // Rounded corners
                                .border(2.dp, MaterialTheme.colorScheme.onBackground) // Border
                                .shadow(5.dp), // Shadow
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = breedDetails.name, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(bottom = 8.dp))

                    Text(text = breedDetails.description, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Text(text = "Origin: ${breedDetails.origin}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Text(text = "Life Span: ${breedDetails.life_span}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Text(text = "Weight: ${breedDetails.weight.metric}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Text(text = "Temperament: ${breedDetails.temperament}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))

                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        BreedTrait("Adaptability", breedDetails.adaptability)
                        BreedTrait("Affection Level", breedDetails.affection_level)
                        BreedTrait("Child Friendly", breedDetails.child_friendly)
                        BreedTrait("Dog Friendly", breedDetails.dog_friendly)
                        BreedTrait("Energy Level", breedDetails.energy_level)
                        BreedTrait("Grooming", breedDetails.grooming)
                        BreedTrait("Health Issues", breedDetails.health_issues)
                    }
                    val context = LocalContext.current

                    Button(
                        onClick = {
                            state.breedsDetail?.wikipedia_url?.let { url ->
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Open Wikipedia Page")
                    }
                }
            }
        }
    }
}

@Composable
fun BreedTrait(name: String, level: Int, maxLevel: Int = 5) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$name:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LinearProgressIndicator(
            progress = level / maxLevel.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            color = when (level) {
                in 0..1 -> MaterialTheme.colorScheme.error
                in 2..3 -> Color.Yellow
                in 4..4 -> Color.Magenta
                else -> Color.Blue
            }
        )
    }
}

