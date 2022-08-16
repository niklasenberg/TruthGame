package se.umu.nien1121.truthgame.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.ActivityMainBinding

/**
 * Primary [AppCompatActivity] for hosting fragments. It's lifecycle determines creation/destruction
 * of [GameViewModel].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init ActionBar to be used in fragments
        setSupportActionBar(findViewById(R.id.toolbar))

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            //Launch LobbyFragment as default
            val fragment = LobbyFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStack()
        } else {
            moveTaskToBack(true)
        }
    }
}