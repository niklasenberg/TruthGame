package se.umu.nien1121.truthgame.controller

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.ActivityWelcomeBinding
import java.io.File


/**
 * Activity presented on launch of application.
 */
class WelcomeActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityWelcomeBinding

    //Inflates layout and launches RulesFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deleteOldPictures()
        setListeners()

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.welcome_fragment_container)
        if (currentFragment == null) {
            val fragment = RulesFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.welcome_fragment_container, fragment)
                .commit()
        }

    }

    /**
     * Sets button listener
     */
    private fun setListeners() {
        binding.buttonDone.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Helper function which deletes all prior player pictures from external storage.
     */
    private fun deleteOldPictures() {
        val dir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/")
        dir.deleteRecursively()
    }
}