package se.umu.nien1121.truthgame.controller

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.FragmentPlayerDetailsBinding
import se.umu.nien1121.truthgame.hideKeyboard
import se.umu.nien1121.truthgame.model.GameViewModel
import se.umu.nien1121.truthgame.model.Player
import se.umu.nien1121.truthgame.setPicture
import se.umu.nien1121.truthgame.setSupportActionBar
import java.io.File
import java.text.DateFormat.getDateInstance


//Constants
private const val PLAYER_INDEX = "se.umu.nien1121.playerIndex"
private const val IMAGE_URI = "se.umu.nien1121.imageUri"

/**
 * Fragment which handles creating and updating [Player] objects
 */
class PlayerDetailsFragment : Fragment() {

    /**
     * Shared ViewModel owned by [MainActivity]
     */
    private val gameViewModel: GameViewModel by activityViewModels()

    /**
     * Determines current [Player] object, or whether player does not exist
     */
    private var playerIndex = -1

    //Player photo and input validation
    private lateinit var imageUri: Uri
    private var hasPhoto = false
    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when {
                it.resultCode != RESULT_OK -> print("Something went wrong")
                it.data != null -> {
                    hasPhoto = setPicture(binding.imageviewPlayer, imageUri, requireContext())
                }
            }
        }

    //ViewBinding
    private var _binding: FragmentPlayerDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Should saved state exists, a URI has already been created
        imageUri = if (savedInstanceState != null) {
            //Read existing URI
            savedInstanceState.getParcelable(IMAGE_URI)!!
        } else {
            //Create new file and URI to store player picture in
            val photoFile = File.createTempFile(
                "JPEG_" + getDateInstance(),
                ".jpg",
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            //Use FileProvider to get URI
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".fileprovider",
                photoFile
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()

        //Get playerIndex from Bundle arguments, load current details if player exists
        playerIndex = arguments?.getInt(PLAYER_INDEX)!!
        if (playerIndex >= 0) {
            getPlayerDetails(playerIndex)
            setSupportActionBar("Update", requireActivity(), viewLifecycleOwner)
        } else {
            setSupportActionBar("Add player", requireActivity(), viewLifecycleOwner)
        }
    }

    /**
     * Retrieves and loads existing [Player] details from [gameViewModel].
     * @param playerIndex: index of [Player] to be displayed
     */
    private fun getPlayerDetails(playerIndex: Int) {
        //Get picture
        imageUri = gameViewModel.getPlayer(playerIndex).imageUri
        hasPhoto = setPicture(binding.imageviewPlayer, imageUri, requireContext())

        //Get name
        binding.textInputLayoutName.apply {
            editText!!.setText(gameViewModel.getPlayer(playerIndex).name)
            jumpDrawablesToCurrentState()
        }

        //Get color
        var colorButtonId = 0
        when (gameViewModel.getPlayer(playerIndex).favouriteColor) {
            R.color.yellow_crayola -> colorButtonId = R.id.radio_yellow_crayola
            R.color.sandy_brown -> colorButtonId = R.id.radio_sandy_brown
            R.color.buff -> colorButtonId = R.id.radio_buff
        }
        binding.radioGroupColor.apply {
            check(colorButtonId)
            jumpDrawablesToCurrentState()
        }

        //Modify button say "Update" instead of "Save"
        binding.buttonDone.text = getString(R.string.update_button)
    }

    /**
     * Helper method for setting view listeners and defaults
     */
    private fun setViews() {
        binding.imageviewPlayer.apply {
            //Setup Camera Intent via ACTION_IMAGE_CAPTURE
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(
                captureImageIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            //Disables button if camera activity has not been completed
            if (resolvedActivity == null) {
                isEnabled = true
            }

            setOnClickListener {
                //Launch intent with URI to place image file in
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                photoLauncher.launch(captureImageIntent)
            }
        }

        //Set picture and input validate via hasPhoto
        hasPhoto = setPicture(binding.imageviewPlayer, imageUri, requireContext())

        //Default check one of the radiobuttons
        binding.radioGroupColor.check(R.id.radio_yellow_crayola)

        binding.buttonDone.setOnClickListener {
            //Force hide keyboard to not persist to LobbyFragment
            hideKeyboard()
            savePlayer()
        }
    }

    /**
     * Helper method for input validating and saving/updating [Player] details.
     */
    private fun savePlayer() {
        if (!hasPhoto) {
            //No photo loaded
            Toast.makeText(context, "Player needs a picture!", Toast.LENGTH_LONG).show()
        } else if (binding.textInputLayoutName.editText!!.text.isEmpty()) {
            //No name entered
            binding.textInputLayoutName.error = "Enter name"
        } else {
            //RadioButtons will always have default choice
            val selectedId = binding.radioGroupColor.checkedRadioButtonId
            var color = 0
            when (selectedId) {
                R.id.radio_yellow_crayola -> color = R.color.yellow_crayola
                R.id.radio_sandy_brown -> color = R.color.sandy_brown
                R.id.radio_buff -> color = R.color.buff
            }

            //Save player via ViewModel
            gameViewModel.savePlayer(
                playerIndex,
                imageUri,
                binding.textInputLayoutName.editText!!.text.toString(),
                color
            )

            //Return to lobby
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //Save already created URI
        outState.putParcelable(IMAGE_URI, imageUri)
    }


    companion object {
        /**
         * Method for creating new [PlayerDetailsFragment].
         * @param playerIndex: if [Player] object exists, this is used to retrieve details via [getPlayerDetails]
         */
        fun newInstance(playerIndex: Int) = PlayerDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(PLAYER_INDEX, playerIndex)
            }
        }
    }
}