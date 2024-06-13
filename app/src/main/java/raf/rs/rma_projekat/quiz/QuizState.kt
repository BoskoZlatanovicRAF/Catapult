package raf.rs.rma_projekat.quiz

import raf.rs.rma_projekat.quiz.model.Question

data class QuizState(
    val isLoading: Boolean = true,
    val isQuizStarted: Boolean = false,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val isQuizCompleted: Boolean = false,
    val errorMessage: String? = null,
    val remainingTime: Long = 300,
    val isQuizExited: Boolean = false
)