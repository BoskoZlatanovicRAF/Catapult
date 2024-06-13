package raf.rs.rma_projekat.quiz

sealed class QuizUiEvent {
    object FetchQuestions : QuizUiEvent()
    object StartQuiz : QuizUiEvent()
    data class AnswerQuestion(val answer: String) : QuizUiEvent()
    object NextQuestion : QuizUiEvent()
    object FinishQuiz : QuizUiEvent()
    object TickTimer : QuizUiEvent()
    object ExitQuiz : QuizUiEvent()
}