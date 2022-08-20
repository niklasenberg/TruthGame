package se.umu.nien1121.truthgame.database

import androidx.lifecycle.LiveData
import androidx.room.*
import se.umu.nien1121.truthgame.model.Question

/**
 * Data access object which defines specific queries to be performed on [QuestionDatabase].
 */
@Dao
interface QuestionDao {
    @Query("SELECT * FROM question")
    fun getQuestions(): LiveData<List<Question>>

    @Query("SELECT * FROM question ORDER BY RANDOM() LIMIT 1;")
    fun getRandomQuestion(): LiveData<Question>

    @Insert
    fun addQuestion(question: Question)

    @Delete
    fun deleteQuestion(question: Question)
}