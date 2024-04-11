package raf.rs.rma_projekat.catbreed.list

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel

fun NavGraphBuilder.catbreeds(
    route: String,
    onCatBreedClick: (String) -> Unit
) = composable(
    route = route
){
    val catListViewModel = viewModel<CatBreedListViewModel>()

    val state = catListViewModel.state.collectAsState()

    CatBreedListScreen(
        state = state.value,
        eventPublisher = {
            catListViewModel.setEvent(it) // osluskujemo eventove
        },
        onCatBreedClick = onCatBreedClick
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatBreedListScreen(
    state: CatBreedListState,
    eventPublisher: (uiEvent: CatBreedListUiEvent) -> Unit,
    onCatBreedClick: (String) -> Unit
) {

    var active by remember { mutableStateOf(false) }
//    val listState = rememberLazyListState()

    BackHandler(enabled = state.searchText.isNotEmpty()) {
        eventPublisher(CatBreedListUiEvent.Search(query = ""))
//        eventPublisher(CatBreedListUiEvent.SubmitSearch(""))
        eventPublisher(CatBreedListUiEvent.ClearSearch)
    }

    Scaffold(
        topBar = {

            SearchBar(
                query = state.searchText,
                onQueryChange = { newValue ->
                    eventPublisher(CatBreedListUiEvent.Search(newValue))
                },
                onSearch = {
                    active = false
                },
                active = active,
                onActiveChange = { isActive ->
                    active = isActive
                },
                placeholder = { Text("Search") },
                leadingIcon = {

                    Icon(Icons.Filled.Search, contentDescription = "Clear")

                },
                trailingIcon = { // ovo je samo za X
                    if(active){
                        Icon(
                            modifier = Modifier.clickable {
                                if(state.searchText.isNotEmpty()){
                                    eventPublisher(CatBreedListUiEvent.ClearSearch)
                                }
                                else{
                                    active = false

                                }

                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                content = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        content = { paddingValues ->
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
                    val errorMessage = when (state.error) {
                        is ListError.FetchError ->
                            "Failed to load. Error message: ${state.error.cause?.message}."
                    }
                    Text(text = errorMessage)
                }
            }else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues,
                ) {
                    val catBreedsToDisplay = if (state.searchMode) state.filteredCatBreeds else state.catBreeds
                    items(catBreedsToDisplay, key = { breed -> breed.id }) { catBreed ->
                        CatBreedItem(
                            catBreed = catBreed,
                            onClick = { onCatBreedClick(catBreed.id) }
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun CatBreedItem(catBreed: CatBreedUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = catBreed.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 2.dp))


            if (catBreed.alt_names.isNotEmpty()) {
                Text(text = catBreed.alt_names, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(2.dp), fontStyle = FontStyle.Italic)
            }

            Text(
                text = if(catBreed.description.length > 250) catBreed.description.substring(0, 250) + "..." else catBreed.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 2.dp)
            )


            val temperaments = catBreed.temperament.split(",").shuffled().take(3)

            Row {

                temperaments.forEach { temperament ->

                    SuggestionChip(
                        onClick = {},
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .padding(end = 8.dp),
                        label = { Text(text = temperament.trim()) }

                    )
                }
            }
        }
    }
}