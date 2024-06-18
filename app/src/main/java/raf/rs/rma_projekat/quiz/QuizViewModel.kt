package raf.rs.rma_projekat.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import raf.rs.rma_projekat.catbreed.api.repository.CatBreedRepository
import raf.rs.rma_projekat.quiz.model.Question
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: CatBreedRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private val _event = MutableSharedFlow<QuizUiEvent>()

    private fun setState(reducer: QuizState.() -> QuizState) = _state.getAndUpdate(reducer)

    private var timerJob: Job? = null


    fun setEvent(event: QuizUiEvent) = viewModelScope.launch {
        _event.emit(event)
    }

    init {
        handleEvents()
        updateMissingImages()
    }

    private fun handleEvents() {
        viewModelScope.launch {
            _event.collect { event ->
                onEvent(event)
            }
        }
    }

    private fun onEvent(event: QuizUiEvent) {
        when (event) {
            is QuizUiEvent.FetchQuestions -> fetchQuestions()
            is QuizUiEvent.StartQuiz -> startQuiz()
            is QuizUiEvent.AnswerQuestion -> answerQuestion(event.answer)
            is QuizUiEvent.NextQuestion -> nextQuestion()
            is QuizUiEvent.FinishQuiz -> finishQuiz()
            is QuizUiEvent.TickTimer -> tickTimer()
            is QuizUiEvent.ExitQuiz -> exitQuiz()
        }
    }

    private fun updateMissingImages() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.updateMissingImages()
                }
                setEvent(QuizUiEvent.FetchQuestions)
            } catch (e: Exception) {
                setState { copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun fetchQuestions() {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = true) }
                val questions = withContext(Dispatchers.IO) { generateQuestions() }
                setState { copy(isLoading = false, questions = questions) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun startQuiz() {
        if (state.value.questions.isEmpty())
            setEvent(QuizUiEvent.FetchQuestions)
        resetQuizState()
        startTimer()
    }

    private fun resetQuizState() {
        setState { copy(isQuizStarted = true, currentQuestionIndex = 0, score = 0, isQuizCompleted = false) }
        if (state.value.isQuizCompleted || state.value.isQuizExited) {
            setState { copy(isQuizCompleted = false, isQuizExited = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (state.value.isQuizCompleted || state.value.isQuizExited) break
                setEvent(QuizUiEvent.TickTimer)
            }
        }
    }

    private fun tickTimer() {
        val remainingTime = state.value.remainingTime - 1
        if (remainingTime <= 0) {
            setEvent(QuizUiEvent.FinishQuiz)
        } else {
            setState { copy(remainingTime = remainingTime) }
        }
    }

    private fun answerQuestion(answer: String) {
        val currentQuestion = state.value.questions[state.value.currentQuestionIndex]
        val score = if (answer == currentQuestion.correctAnswer) state.value.score + 1 else state.value.score
        setState { copy(score = score) }
    }

    private fun nextQuestion() {
        val nextIndex = state.value.currentQuestionIndex + 1
        if (nextIndex < state.value.questions.size) {
            setState { copy(currentQuestionIndex = nextIndex) }
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        setState { copy(isQuizCompleted = true) }
    }

    private fun exitQuiz() {
        timerJob?.cancel()
        resetEverything()
    }

    private fun resetEverything() {
        setState {
            copy(
                isQuizExited = true,
                isQuizStarted = false,
                isQuizCompleted = false,
                currentQuestionIndex = 0,
                score = 0,
                remainingTime = 300,
                questions = emptyList()
            )
        }
    }



    private fun generateQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val breeds = repository.getAllCatBreeds().filter { it.id != "mala" }

        val allTemperaments = breeds.flatMap { it.temperament.split(",") }
            .map {
                it.trim().lowercase()
                    .replaceFirstChar { char -> char.titlecase(Locale.getDefault()) }
            }
            .toSet()

        val selectedBreeds = breeds.shuffled().distinct().take(20)
        val addedImages = mutableListOf<String>()

        selectedBreeds.forEachIndexed { index, breed ->
            val breedImages = repository.getBreedImages(breed.id).filter { it.breedId == breed.id }.shuffled()
            var selectedImage = breedImages.random()

            while (selectedImage.url in addedImages) {
                selectedImage = breedImages.random()
            }

            when {
                index < 6 -> {
                    val incorrectBreeds = breeds.filter { it.id != breed.id }.shuffled().take(3).map { it.name }
                    val answers = (incorrectBreeds + breed.name).shuffled()
                    questions.add(
                        Question(
                            imageUrl = selectedImage.url,
                            questionText = "What breed is the cat?",
                            answers = answers,
                            correctAnswer = breed.name
                        )
                    )
                }
                index < 13 -> {
                    val breedTemperaments = breed.temperament.split(",").map { it.trim() }.toSet()
                    val incorrectTemperament = allTemperaments.filterNot { it in breedTemperaments }.random()
                    val answers = (breedTemperaments.shuffled().take(3) + incorrectTemperament).shuffled()
                    questions.add(
                        Question(
                            imageUrl = selectedImage.url,
                            questionText = "Find the odd temperament out!",
                            answers = answers,
                            correctAnswer = incorrectTemperament
                        )
                    )
                }
                index < 20 -> {
                    val correctTemperament = breed.temperament.split(",").random().trim()
                    val incorrectTemperaments = allTemperaments.filterNot { it == correctTemperament }.shuffled().take(3)
                    val answers = (incorrectTemperaments + correctTemperament).shuffled()
                    questions.add(
                        Question(
                            imageUrl = selectedImage.url,
                            questionText = "Which temperament belongs to the given cat?",
                            answers = answers,
                            correctAnswer = correctTemperament
                        )
                    )
                }
            }
        }

        return questions.distinctBy { it.imageUrl }.shuffled().take(20)
    }
}
