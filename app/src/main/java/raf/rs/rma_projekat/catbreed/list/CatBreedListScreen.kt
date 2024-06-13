package raf.rs.rma_projekat.catbreed.list

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rs.rma_projekat.catbreed.list.model.CatBreedUiModel
import raf.rs.rma_projekat.core.theme.poppinsBold
import raf.rs.rma_projekat.core.theme.poppinsItalic
import raf.rs.rma_projekat.core.theme.poppinsLight
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.core.theme.poppinsRegular
import raf.rs.rma_projekat.core.theme.poppinsThin
import rs.edu.raf.rma.R

fun NavGraphBuilder.catbreeds(
    route: String,
    onCatBreedClick: (String) -> Unit
) = composable(
    route = route
){
    val catListViewModel = hiltViewModel<CatBreedListViewModel>()

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

    BackHandler(enabled = state.searchText.isNotEmpty()) {
        eventPublisher(CatBreedListUiEvent.Search(query = ""))
        eventPublisher(CatBreedListUiEvent.ClearSearch)
    }

    Scaffold(
        topBar = {
            CustomSearchBar(
                query = state.searchText,
                onQueryChange = { newValue ->
                    eventPublisher(CatBreedListUiEvent.Search(newValue))
                },
                onClear = {
                    eventPublisher(CatBreedListUiEvent.ClearSearch)
                },
                onSearch = {
                    active = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
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
            Text(text = catBreed.name, style = poppinsBold, modifier = Modifier.padding(bottom = 2.dp))


            if (catBreed.alt_names.isNotEmpty()) {
                Text(text = catBreed.alt_names, style = poppinsLight, modifier = Modifier.padding(bottom = 4.dp, start = 1.dp))
            }

            Text(
                text = if(catBreed.description.length > 250) catBreed.description.substring(0, 250) + "..." else catBreed.description,
                style = poppinsMedium,
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
                        label = { Text(text = temperament.trim(), style = poppinsRegular) }

                    )
                }
            }
        }
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.DarkGray, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                textStyle = TextStyle(color = Color.White, fontStyle = poppinsMedium.fontStyle),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                        keyboardController?.hide()
                    }
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text("Search", color = Color.Gray, style = poppinsRegular)
                    }
                    innerTextField()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
