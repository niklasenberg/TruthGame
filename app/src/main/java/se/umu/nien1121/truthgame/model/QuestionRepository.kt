package se.umu.nien1121.truthgame.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import se.umu.nien1121.truthgame.database.QuestionDatabase
import java.util.concurrent.Executors


private const val DATABASE_NAME = "question-database"

/**
 * Repository which acts as API to Room database by interaction with DAO. Uses several threads.
 */
class QuestionRepository private constructor(context: Context) {

    //Build DB
    private val database: QuestionDatabase = Room.databaseBuilder(
        context.applicationContext,
        QuestionDatabase::class.java,
        DATABASE_NAME
    ).build()

    //Get dao
    private val questionDao = database.questionDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getQuestions(): LiveData<List<Question>> = questionDao.getQuestions()

    fun getRandomQuestion(): LiveData<Question> = questionDao.getRandomQuestion()

    //Uses separate thread to not suspend UI
    fun addQuestion(question: Question) {
        executor.execute {
            questionDao.addQuestion(question)
        }
    }

    //Uses separate thread to not suspend UI
    fun deleteQuestion(question: Question) {
        executor.execute {
            questionDao.deleteQuestion(question)
        }
    }

    //Singleton pattern to ensure only one instance of database is created, as more is not needed
    companion object {
        private var INSTANCE: QuestionRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = QuestionRepository(context)
            }
        }

        fun get(): QuestionRepository {
            return INSTANCE ?: throw IllegalStateException("QuestionRepository must be initialized")
        }
    }
}