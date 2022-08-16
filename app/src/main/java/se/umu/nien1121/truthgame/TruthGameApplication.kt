package se.umu.nien1121.truthgame

import android.app.Application
import android.os.Environment
import android.util.Log
import se.umu.nien1121.truthgame.model.QuestionRepository
import java.io.File


/**
 * [Application] class which ensures initialisation of database
 */
class TruthGameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Init database
        QuestionRepository.initialize(this)
    }
}