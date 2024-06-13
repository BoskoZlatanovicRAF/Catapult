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
//        Log.d("QuizViewModel", "Event received: $event")
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

    private fun updateMissingImages() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    Log.d("QuizViewModel", "updateMissingImages()")
                    repository.updateMissingImages()
                }
                onEvent(QuizUiEvent.FetchQuestions)
            } catch (e: Exception) {
                setState { copy(isLoading = false, errorMessage = e.message) }
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

    private fun exitQuiz() {
        timerJob?.cancel()
        resetEverything()
//        setState { copy(isQuizStarted == false) }
    }

    private fun resetEverything() {
        setState { copy(isQuizExited = true, isQuizStarted = false, isQuizCompleted = false, currentQuestionIndex = 0, score = 0, remainingTime = 300, questions = emptyList()) }
//        setState { copy(isQuizExited = false)}
    }

    private fun fetchQuestions() {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = true) }
                // Fetch and generate questions here

                    val questions = generateQuestions()
                    setState { copy(isLoading = false, questions = questions) }

            } catch (e: Exception) {
                setState { copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private suspend fun generateQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        withContext(Dispatchers.IO) {

            Log.d("QuizViewModel", "Generating questions")

            val breeds = repository.getAllCatBreeds().filter { it.id != "mala" } // Fetch all breeds

            val allTemperaments = breeds.flatMap { it.temperament.split(",") }
                .map {
                    it.trim().lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
                .toSet()
            val selectedBreeds = breeds.shuffled().distinct().take(20)
            val addedImages = mutableListOf<String>()
            selectedBreeds.forEachIndexed { index, breed ->
                val breedImages =
                    repository.getBreedImages(breed.id).filter { it.breedId == breed.id }.shuffled()
                var selectedImage = breedImages.random()

                while (selectedImage.url in addedImages) {
                    selectedImage = breedImages.random()
                }

                // Question 1: What breed is the cat?
                if (index < 6) {
                    val incorrectBreeds =
                        breeds.filter { it.id != breed.id }.shuffled().take(3).map { it.name }
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

                // Question 2: Find the odd temperament out
                if (index < 13) {
                    val breedTemperaments = breed.temperament.split(",").map { it.trim() }.toSet()
                    val incorrectTemperament =
                        allTemperaments.filterNot { it in breedTemperaments }.random()
                    val answers =
                        (breedTemperaments.shuffled().take(3) + incorrectTemperament).shuffled()
                    questions.add(
                        Question(
                            imageUrl = selectedImage.url,
                            questionText = "Find the odd temperament out!",
                            answers = answers,
                            correctAnswer = incorrectTemperament
                        )
                    )
                }

                // Question 3: Which temperament belongs to the given cat?
                if (index < 20) {
                    val correctTemperament = breed.temperament.split(",").random().trim()
                    val incorrectTemperaments =
                        allTemperaments.filterNot { it == correctTemperament }.shuffled().take(3)
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
        val distinctQuestions = questions.distinctBy { it.imageUrl }
        return distinctQuestions.shuffled().take(20)

    }

    private fun startQuiz() {
        Log.d("QuizViewModel", "Starting quiz")
//        resetEverything()

        if(state.value.questions.isEmpty())
            setEvent(QuizUiEvent.FetchQuestions)
        setState { copy(isQuizStarted = true, currentQuestionIndex = 0, score = 0, isQuizCompleted = false, ) }
        if(state.value.isQuizCompleted || state.value.isQuizExited){
            setState { copy(isQuizCompleted = false, isQuizExited = false) }
        }

        startTimer()
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
            setState { copy(isQuizCompleted = true) }
        }
    }

    private fun finishQuiz() {
        setState { copy(isQuizCompleted = true) }
    }

    private fun tickTimer() {

        val remainingTime = state.value.remainingTime - 1

        if (remainingTime <= 0) {
            setEvent(QuizUiEvent.FinishQuiz)
        } else {
            setState { copy(remainingTime = remainingTime) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if(state.value.isQuizCompleted || state.value.isQuizExited) break
                setEvent(QuizUiEvent.TickTimer)
            }
        }
        if (state.value.isQuizCompleted || state.value.isQuizExited) timerJob?.cancel()
    }

    private fun calculateTotalPoints(): Float {
        val nca = state.value.score.toFloat()
        val mdq = 300.00f
        val rdt = state.value.remainingTime.toFloat()
        val tnp = nca * 2.5f * (1 + (rdt + 120) / mdq)
        return tnp.coerceAtMost(100.00f)
    }
}
