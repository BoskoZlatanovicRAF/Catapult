package raf.rs.rma_projekat.leaderboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardEntry
import java.sql.Date


fun NavGraphBuilder.leaderBoard(
    route: String
) = composable(
    route = route
) {
    val leaderBoardViewModel = hiltViewModel<LeaderBoardViewModel>()
    val state = leaderBoardViewModel.state.collectAsState()

    LeaderBoardScreen(
        state = state.value,
        eventPublisher = {
            leaderBoardViewModel.setEvent(it)
        }
    )
}

@Composable
fun LeaderBoardScreen(
    state: LeaderBoardState,
    eventPublisher: (LeaderBoardUiEvent) -> Unit
) {
    if (state.error != null) {
        Text("Error: ${state.error}")
    } else {
        LazyColumn {
            items(state.leaderBoard) { entry ->
                LeaderBoardItem(entry)
            }
        }
    }
}

@Composable
fun LeaderBoardItem(entry: LeaderBoardEntry) {
    val date = Date(entry.createdAt)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nickname: ${entry.nickname}")
        Text("Score: ${entry.result}")
        Text("Date: $date")
//        Text("Category: ${entry.category}")
    }
}
