package se.umu.nien1121.truthgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Primary [AppCompatActivity] for hosting fragments. It's lifecycle determines creation/destruction
 * of [GameViewModel].
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}