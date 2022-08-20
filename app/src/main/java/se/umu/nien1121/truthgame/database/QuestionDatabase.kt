package se.umu.nien1121.truthgame.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import se.umu.nien1121.truthgame.model.Question

@Database(entities = [Question::class] , version = 1, exportSchema = false)
@TypeConverters(QuestionTypeConverters::class)

/**
 * Database superclass which specifies its DAO
 */
abstract class QuestionDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}