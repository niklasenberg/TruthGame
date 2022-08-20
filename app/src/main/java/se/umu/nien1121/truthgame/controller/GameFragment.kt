package se.umu.nien1121.truthgame.controller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.FragmentGameBinding
import se.umu.nien1121.truthgame.model.GameViewModel
import se.umu.nien1121.truthgame.model.Player
import se.umu.nien1121.truthgame.setPicture
import se.umu.nien1121.truthgame.setSupportActionBar
import kotlin.math.sqrt

private const val QUESTION_TEXT_KEY = "se.umu.nien1121.questionText"

/**
 * Fragment presented when playing game, launched from [LobbyFragment]
 */
class GameFragment : Fragment() {

    /**
     * Shared ViewModel owned by [MainActivity]
     */
    private val gameViewModel: GameViewModel by activityViewModels()

    //Game state properties
    private lateinit var currentPlayer: Player
    private lateinit var questionText: String

    //SensorEvent properties
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    //ViewBinding
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    /**
     * Custom [SensorEventListener] used to handle shake events via [SensorEventListener.onSensorChanged]
     * Source: [tutorialspoint.com](https://www.tutorialspoint.com/how-to-detect-shake-events-in-kotlin)
     */
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            //Get event values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            //Calculate current acceleration and save prior
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            //Check difference between current and prior acceleration
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            //If threshold is reached, fetch new question
            if (acceleration > 10) {
                gameViewModel.getNewQuestion()
            }
        }

        //Required by interface
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShakeSensor()
    }

    /**
     * Helper function which resets acceleration values of sensor and register listener to [sensorManager]
     * Source: [tutorialspoint.com](https://www.tutorialspoint.com/how-to-detect-shake-events-in-kotlin)
     */
    private fun setShakeSensor() {
        //Get SensorManager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager!!.registerListener(
            sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Sets actionbar, views, listeners and handles [savedInstanceState]
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSupportActionBar("", requireActivity(), viewLifecycleOwner)
        updateUI()
        setListeners()

        //If savedState exists, load already given question
        if (savedInstanceState != null) {
            binding.questionTextView.text = savedInstanceState.getString(QUESTION_TEXT_KEY)
        } else {
            //Else fetch new
            gameViewModel.getNewQuestion()
        }
    }

    /**
     * Helper function which configures and displays given data in current view
     */
    private fun updateUI() {
        //Fetch current player objecy
        currentPlayer = gameViewModel.getCurrentPlayer()

        //Set pplayer picture and name text
        setPicture(binding.playerImageView, currentPlayer.imageUri, requireContext())
        binding.playerTextView.text = currentPlayer.name
    }

    /**
     * Helper function used to enable/disable game
     * @param enabled: enables or disables game buttons
     */
    private fun toggleButtons(enabled: Boolean) {
        binding.thumbsUpButton.isEnabled = enabled
        binding.thumbsDownButton.isEnabled = enabled
    }

    /**
     * Set up view listeners and Question observer
     */
    private fun setListeners() {
        //Button listeners
        binding.thumbsDownButton.setOnClickListener {
            gameViewModel.nextRound(false)
            updateUI()
        }
        binding.thumbsUpButton.setOnClickListener {
            gameViewModel.nextRound(true)
            updateUI()
        }

        //Observe LiveData in order update question
        gameViewModel.questionLiveData.observe(viewLifecycleOwner) { question ->
            //If questions exist
            if (question != null) {
                binding.questionTextView.text = question.content
                questionText = question.content
                toggleButtons(true)
            } else {
                //Else tell user they need to register questions, disable game
                binding.questionTextView.text = getString(R.string.you_need_to_add_questions)
                toggleButtons(false)
            }
        }
    }

    /**
     * Removes current [sensorListener] from [sensorManager]
     * Source: [tutorialspoint.com](https://www.tutorialspoint.com/how-to-detect-shake-events-in-kotlin)
     */
    override fun onPause() {
        //onPause remove current sensorListener from manager
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    /**
     * Assigns [sensorListener] to [sensorManager]
     * Source: [tutorialspoint.com](https://www.tutorialspoint.com/how-to-detect-shake-events-in-kotlin)
     */
    override fun onResume() {
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    /**
     * Saves current [questionText]
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //If any question exists in database
        if (this::questionText.isInitialized) {
            outState.putString(QUESTION_TEXT_KEY, questionText)
        }
    }

    companion object {
        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }
}