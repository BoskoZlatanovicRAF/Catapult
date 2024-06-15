package raf.rs.rma_projekat.quiz.model

data class Question(
    val imageUrl: String,
    val questionText: String,
    val answers: List<String>,
    val correctAnswer: String
)

