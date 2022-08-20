package se.umu.nien1121.truthgame.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.ActivityMainBinding

/**
 * Primary [AppCompatActivity] for hosting fragments. It's lifecycle determines creation/destruction
 * of [se.umu.nien1121.truthgame.model.GameViewModel].
 */
class MainActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init ActionBar to be used in fragments
        setSupportActionBar(binding.toolbar)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            //Launch LobbyFragment as default
            val fragment = LobbyFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    /**
     * Disables Activity from finishing on press of back button when backstack is empty
     * thus preserving state of ViewModel owned by activity
     */
    override fun onBackPressed() {
        //If not in lobby, go back
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            //Else move task
            moveTaskToBack(true)
        }
    }
}