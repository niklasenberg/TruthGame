package se.umu.nien1121.truthgame.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import se.umu.nien1121.truthgame.database.QuestionDatabase
import java.util.concurrent.Executors

private const val DATABASE_NAME = "question-database"

/**
 * Repository which acts as API to Room database by interaction with DAO
 */
class QuestionRepository private constructor(context: Context) {

    //Build DB TODO: PREPOPULATE WITH CREATEFROMASSET
    private val database: QuestionDatabase = Room.databaseBuilder(context.applicationContext,
    QuestionDatabase::class.java,
    DATABASE_NAME).build()

    //Get dao
    private val questionDao = database.questionDao()

    //Creating, updating and deleting objects should be done on separate thread
    private val executor = Executors.newSingleThreadExecutor()

    fun getQuestions(): LiveData<List<Question>> = questionDao.getQuestions()

    fun getRandomQuestion(): LiveData<Question> = questionDao.getRandomQuestion()

    fun addQuestion(question: Question){
        //Uses separate thread to not suspend UI
        executor.execute{
            questionDao.addQuestion(question)
        }
    }

    //Singleton pattern to ensure only one instance of database is created
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