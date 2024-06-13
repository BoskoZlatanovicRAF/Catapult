package raf.rs.rma_projekat.quiz

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import raf.rs.rma_projekat.core.theme.poppinsBold
import raf.rs.rma_projekat.core.theme.poppinsMedium
import raf.rs.rma_projekat.core.theme.poppinsRegular

fun NavGraphBuilder.quiz(
    route: String
) = composable(
    route = route
) {
    val quizViewModel = hiltViewModel<QuizViewModel>()

    val state = quizViewModel.state.collectAsState()


    QuizScreen(
        state = state.value,
        eventPublisher = {
            quizViewModel.setEvent(it)
        }
    )
}

@Composable
fun QuizScreen(
    state: QuizState,
    eventPublisher: (QuizUiEvent) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var isAnswerClickable by remember { mutableStateOf(true) }

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
//                Log.d("QuizScreen", "Start Quiz button clicked")
                eventPublisher(QuizUiEvent.StartQuiz)
            }
        )
    } else if (state.isQuizCompleted) {
        val totalPoints = calculateTotalPoints(state.score, state.remainingTime)
        QuizResultScreen(totalPoints)
    } else if (state.isQuizStarted && !state.isQuizCompleted) {
//        Log.d("Current question index:", "${state.currentQuestionIndex}")
//        Log.d("Questions size: ", "${state.questions}")
        val currentQuestion = state.questions[state.currentQuestionIndex]
        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        var showResult by remember { mutableStateOf(false) }

        BackHandler {
            showDialog = true
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(onClick = { showDialog = true}) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit Quiz")
            }
            Timer(remainingTime = state.remainingTime)
            Spacer(modifier = Modifier.height(16.dp))
            Text(currentQuestion.questionText, style = poppinsMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(currentQuestion.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            currentQuestion.answers.forEach { answer ->
                val isCorrect = answer == currentQuestion.correctAnswer
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
                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
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
            style = TextStyle(fontSize = 20.sp, color = Color.White)
        )
    }
}

@Composable
fun QuizLandingPage(onStartQuiz: () -> Unit) {
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
    }
}

@Composable
fun QuizResultScreen(totalPoints: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Your score: ${totalPoints.coerceAtMost(100.00f)}", style = poppinsBold)
    }
}

fun calculateTotalPoints(correctAnswers: Int, remainingTime: Long): Float {
    val nca = correctAnswers.toFloat()
    val mdq = 300.00f
    val rdt = remainingTime.toFloat()
    val tnp = nca * 2.5f * (1 + (rdt + 120) / mdq)
    return tnp.coerceAtMost(100.00f)
}

