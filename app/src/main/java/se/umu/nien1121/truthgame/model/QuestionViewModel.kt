package se.umu.nien1121.truthgame.model

import androidx.lifecycle.ViewModel

/**
 * ViewModel used to present, add and delete [Question] objects.
 */
class QuestionViewModel : ViewModel() {

    //Repository
    private var questionRepository: QuestionRepository = QuestionRepository.get()

    //LiveData for list
    val questionListLiveData = questionRepository.getQuestions()

    fun addQuestion(question: Question) {
        questionRepository.addQuestion(question)
    }

    fun deleteQuestion(question: Question) {
        questionRepository.deleteQuestion(question)
    }
}