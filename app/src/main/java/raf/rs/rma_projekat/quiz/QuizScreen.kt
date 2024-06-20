package raf.rs.rma_projekat.quiz

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import raf.rs.rma_projekat.core.theme.poppinsBold
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.core.theme.poppinsRegular
import raf.rs.rma_projekat.leaderboard.LeaderBoardUiEvent
import raf.rs.rma_projekat.leaderboard.LeaderBoardViewModel
import raf.rs.rma_projekat.navigation_bar.screens.Screen
import raf.rs.rma_projekat.utility.hideSystemBars
import raf.rs.rma_projekat.utility.showSystemBars
import kotlin.math.round

fun NavGraphBuilder.quiz(
    route: String,
    navController: NavController
) = composable(
    route = route
) {
    val quizViewModel = hiltViewModel<QuizViewModel>()
    val state = quizViewModel.state.collectAsState()
    QuizScreen(
        state = state.value,
        eventPublisher = {
            quizViewModel.setEvent(it)
        },
        navController = navController
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun QuizScreen(
    state: QuizState,
    eventPublisher: (QuizUiEvent) -> Unit,
    navController: NavController
) {

    val activity = LocalContext.current as? Activity
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Exit Quiz") },
            text = { Text("Do you want to exit the quiz?") },
            confirmButton = {
                Button(onClick = {
                    eventPublisher(QuizUiEvent.ExitQuiz)
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    LaunchedEffect(state.isQuizStarted, state.isQuizCompleted) {
        if (state.isQuizStarted && !state.isQuizCompleted) {
            activity?.hideSystemBars()
        } else {
            activity?.showSystemBars()
        }
    }
    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (!state.isQuizStarted) {
        QuizLandingPage(
            onStartQuiz = {
                eventPublisher(QuizUiEvent.StartQuiz)
            },
            navController = navController
        )
    } else if (state.isQuizCompleted) {
        val totalPoints = calculateTotalPoints(state.score, state.remainingTime)
        QuizResultScreen(totalPoints, eventPublisher, navController)
    } else {
        QuizQuestions(state, eventPublisher)
    }
}

@Composable
fun QuizLandingPage(onStartQuiz: () -> Unit , navController: NavController) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Cat Quiz!", style = poppinsRegular)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onStartQuiz) {
                Text("Start the Quiz!")
            }
        }
        IconButton(onClick = { navController.navigate(Screen.CatBreeds.route) }, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Back")
        }
    }
}

@Composable
fun QuizResultScreen(
    totalPoints: Float,
    eventPublisher: (QuizUiEvent) -> Unit,
    navController: NavController
) {
    val leaderBoardViewModel = hiltViewModel<LeaderBoardViewModel>()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Your score: $totalPoints", style = poppinsBold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { eventPublisher(QuizUiEvent.ExitQuiz) }){
                Text("Restart Quiz")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                leaderBoardViewModel.setEvent(LeaderBoardUiEvent.PostResult(totalPoints))
                navController.navigate(Screen.LeaderBoard.route)
            }) {
                Text("Share Result")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                leaderBoardViewModel.saveUnpublishedResult(totalPoints)
                navController.navigate(Screen.LeaderBoard.route)
            }) {
                Text("Save Result Locally")
            }
        }

        IconButton(onClick = { navController.navigate(Screen.CatBreeds.route) }, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Back")
        }
    }
}



@Composable
fun QuizQuestions(
    state: QuizState,
    eventPublisher: (QuizUiEvent) -> Unit
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isAnswerClickable by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    val currentQuestion = state.questions[state.currentQuestionIndex]

    BackHandler { showDialog = true }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Exit Quiz") },
            text = { Text("Do you want to exit the quiz?") },
            confirmButton = {
                Button(onClick = {
                    eventPublisher(QuizUiEvent.ExitQuiz)
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null) {
            isAnswerClickable = false
            delay(500) // Delay to show the result
            eventPublisher(QuizUiEvent.NextQuestion)
            selectedAnswer = null
            showResult = false
            isAnswerClickable = true
        }
    }
    Crossfade(targetState = currentQuestion) { question ->
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Timer(remainingTime = state.remainingTime)
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit Quiz")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(question.questionText, style = poppinsMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(question.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                question.answers.forEach { answer ->
                    val isCorrect = answer == question.correctAnswer
                    val backgroundColor = when {
                        !showResult -> ButtonDefaults.buttonColors().containerColor
                        isCorrect -> Color.Green
                        else -> Color.Red
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!showResult) {
                                    selectedAnswer = answer
                                    showResult = true
                                    eventPublisher(QuizUiEvent.AnswerQuestion(answer))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                            //                    enabled = isAnswerClickable
                        ) {
                            Text(answer, style = poppinsRegular)
                        }
                        if (showResult) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Transparent)
                                    .clickable(enabled = false) {}
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun Timer(remainingTime: Long) {
    val totalDuration = 300
    val progress = remainingTime.toFloat() / totalDuration.toFloat()
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(100.dp),
            color = Color.Green,
            strokeWidth = 8.dp,
        )
        Text(
            text = String.format("%02d:%02d", remainingTime / 60, remainingTime % 60),
            style = TextStyle(fontSize = 20.sp)
        )
    }
}


fun calculateTotalPoints(correctAnswers: Int, remainingTime: Long): Float {
    val nca = correctAnswers.toFloat() // Number of correct answers
    val mdq = 300.00f // Maximum duration of the quiz
    val rdt = remainingTime.toFloat() // Remaining duration of the quiz
    val tnp = nca * 2.5f * (1 + (rdt + 120) / mdq) // Total number of points
    return round(tnp.coerceAtMost(100.00f) * 100) / 100
}

