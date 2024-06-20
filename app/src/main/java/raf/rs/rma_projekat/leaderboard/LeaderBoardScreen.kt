package raf.rs.rma_projekat.leaderboard

import android.util.Log
import androidx.annotation.ColorInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LeaveBagsAtHome
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.twotone.Leaderboard
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import raf.rs.rma_projekat.core.theme.poppinsBold
import raf.rs.rma_projekat.core.theme.poppinsLight
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.core.theme.poppinsRegular
import raf.rs.rma_projekat.leaderboard.model.LeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.model.PublishedLeaderBoardUiModel
import raf.rs.rma_projekat.leaderboard.model.UnpublishedLeaderBoardUiModel
import java.time.format.TextStyle
import kotlin.math.absoluteValue

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
fun LeaderBoardScreen(state: LeaderBoardState, eventPublisher: (LeaderBoardUiEvent) -> Unit) {
    var selectedTab by remember { mutableStateOf("All Results") }

    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = { selectedTab = "All Results"; eventPublisher(LeaderBoardUiEvent.FetchLeaderBoard) }) {
                Icon(imageVector = Icons.Default.Leaderboard, contentDescription = "All Results")
            }
            IconButton(onClick = { selectedTab = "Published Results"; eventPublisher(LeaderBoardUiEvent.GetPublishedResults) }) {
                Icon(imageVector = Icons.Outlined.Leaderboard, contentDescription = "Published Results")
            }
            IconButton(onClick = { selectedTab = "Unpublished Results"; eventPublisher(LeaderBoardUiEvent.GetUnpublishedResults) }) {
                Icon(imageVector = Icons.TwoTone.Leaderboard, contentDescription = "Unpublished Results")
            }
        }

        when (selectedTab) {
            "All Results" -> {
                Column {
                    TextContainer("Global Leaderboard")
                    LazyColumn {
                        itemsIndexed(state.leaderBoard) { index, entry ->
//                            Log.d("LeaderBoardScreen", "All Results: ${state.leaderBoard}")
                            when (index) {
                                0 -> TopPlayerItem(entry)
                                1 -> SecondAndThirdPlayerItem(entry, 80.dp, 16.sp)
                                2 -> SecondAndThirdPlayerItem(entry, 60.dp, 14.sp)
                                else -> LeaderBoardCardItem(entry)
                            }
                        }
                    }
                }
            }
            "Published Results" -> {
                Column {
                    TextContainer("Published Results")
                    LazyColumn {
                        items(state.publishedResults) { entry ->
                            PublishedLeaderBoardItem(entry, state.leaderBoard)
//                            Log.d("LeaderBoardScreen", "Published Results: ${state.leaderBoard}")
                        }
                    }
                }
            }
            "Unpublished Results" -> {
                Column {
                    TextContainer("Unpublished Results")
                    LazyColumn {
                        items(state.unpublishedResults) { entry ->
                            UnpublishedLeaderBoardItem(entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextContainer(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = poppinsMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun TopPlayerItem(entry: LeaderBoardUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        BadgedBox(
            badge = {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Top Player")
            }
        ) {
            UserHead(nickname = entry.nickname, size = 100.dp)
        }
        Text(text = entry.nickname, style = poppinsBold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp))
        Text(text = "${entry.score}", style = poppinsMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun SecondAndThirdPlayerItem(entry: LeaderBoardUiModel, size: Dp, fontSize: TextUnit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        UserHead(nickname = entry.nickname, size = size)
        Text(text = entry.nickname, style = poppinsBold, fontSize = fontSize, modifier = Modifier.padding(top = 8.dp))
        Text(text = "${entry.score}", style = poppinsMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun LeaderBoardCardItem(entry: LeaderBoardUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${entry.globalRank}",
                style = poppinsMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            UserHead(nickname = entry.nickname, size = 40.dp)
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)  // This will make the column take as much space as it can
            ) {
                Text(text = entry.nickname, style = poppinsBold)
                Text(text = "${entry.date} - ${entry.quizzesPlayed}", style = poppinsRegular, fontSize = 11.sp)
            }
            Text(
                text = "${entry.score}",
                style = poppinsBold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun PublishedLeaderBoardItem(entry: PublishedLeaderBoardUiModel, leaderBoard: List<LeaderBoardUiModel>) {
    LeaderBoardCardItem(
        LeaderBoardUiModel(
            globalRank = leaderBoard.indexOfFirst { it.nickname == entry.nickname } + 1,
            nickname = entry.nickname,
            score = entry.score,
            date = entry.date,
            quizzesPlayed = 0
        )
    )
}

@Composable
fun UnpublishedLeaderBoardItem(entry: UnpublishedLeaderBoardUiModel) {
    LeaderBoardCardItem(
        LeaderBoardUiModel(
            globalRank = 0,
            nickname = entry.nickname,
            score = entry.score,
            date = entry.date,
            quizzesPlayed = 0
        )
    )
}

@Composable
fun UserHead(
    nickname: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        val color = remember(nickname) {
            Color(nickname.toHslColor())
        }
        val initials = nickname.take(2).uppercase() // Taking first two letters of the nickname
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(SolidColor(color))
        }
        Text(text = initials, style = poppinsBold, color = Color.White)
    }
}

@ColorInt
fun String.toHslColor(saturation: Float = 0.5f, lightness: Float = 0.4f): Int {
    val hue = fold(0) { acc, char -> char.code + acc * 37 } % 360
    return ColorUtils.HSLToColor(floatArrayOf(hue.absoluteValue.toFloat(), saturation, lightness))
}



