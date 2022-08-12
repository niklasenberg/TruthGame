package se.umu.nien1121.truthgame

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import se.umu.nien1121.truthgame.databinding.FragmentPlayerDetailsBinding
import java.io.File
import java.text.DateFormat.getDateInstance

//Constants
private const val LOG = "PlayerDetailsFragment"
private const val PLAYER_INDEX = "se.umu.nien1121.playerIndex"
private const val IMAGE_URI = "se.umu.nien1121.imageUri"

class PlayerDetailsFragment : Fragment() {

    /**
     * Shared ViewModel owned by [MainActivity]
     */
    private val gameModel: GameViewModel by activityViewModels()

    /**
     * Determines current [Player] object, or whether player does not exist
     */
    private var playerIndex = -1

    //Input validation
    private lateinit var photoUri: Uri
    private var hasPhoto = false

    //ViewBinding
    private var _binding: FragmentPlayerDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            photoUri = savedInstanceState.getParcelable(IMAGE_URI)!!
            hasPhoto = true
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
        setActionBar()
        setViews()

        //Get playerIndex from Bundle arguments, load current details if player exists
        playerIndex = arguments?.getInt(PLAYER_INDEX)!!
        if (playerIndex >= 0) {
            getPlayerDetails(playerIndex)
        }
    }

    /**
     * Retrieves and loads existing [Player] details from [gameModel].
     * @param playerIndex: index of [Player] to be displayed
     */
    private fun getPlayerDetails(playerIndex: Int) {
        //Get picture
        photoUri = gameModel.getPlayerImage(playerIndex)
        setPicture(binding.imageviewPlayer, photoUri, requireContext())
        hasPhoto = true

        //Get name
        binding.textInputLayoutName.editText!!.setText(gameModel.getPlayerName(playerIndex))

        //Get color
        var colorButtonId = 0
        when (gameModel.getPlayerColor(playerIndex)) {
            R.color.yellow_crayola -> colorButtonId = R.id.radio_yellow_crayola
            R.color.sandy_brown -> colorButtonId = R.id.radio_sandy_brown
            R.color.buff -> colorButtonId = R.id.radio_buff
        }
        binding.radioGroupColor.check(colorButtonId)

        //Modify button say "Update" instead of "Save"
        binding.buttonDone.text = getString(R.string.update_button)
    }

    /**
     * Helper method for setting view listeners and defaults
     */
    private fun setViews() {
        binding.imageviewPlayer.apply {
            if (!hasPhoto) {
                val photoFile = File.createTempFile(
                    "JPEG_" + getDateInstance(),
                    ".jpg",
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".fileprovider",
                    photoFile
                )
            }
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(
                captureImageIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            if (resolvedActivity == null) {
                isEnabled = true
            }

            setOnClickListener {
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                photoLauncher.launch(captureImageIntent)
            }
        }

        //Default check
        binding.radioGroupColor.check(R.id.radio_yellow_crayola)

        binding.buttonDone.setOnClickListener {
            hideKeyboard()
            savePlayer()
        }
    }

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when {
                it.resultCode != RESULT_OK -> print("Something went wrong")
                it.data != null -> {
                    setPicture(binding.imageviewPlayer, photoUri, requireContext())
                }
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
            gameModel.savePlayer(
                playerIndex,
                photoUri,
                binding.textInputLayoutName.editText!!.text.toString(),
                color
            )

            //Return to lobby
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    /**
     * Helper method for configuring the ActionBar, setting the correct title and navigational options
     */
    private fun setActionBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                // Clear current options, set title and add correct menu options
                menu.clear()
                (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(
                    true
                )
                (requireActivity() as AppCompatActivity).supportActionBar!!.setTitle(
                    if (playerIndex < 0) R.string.add_player_text else R.string.update_player_text
                )
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                when (item.itemId) {
                    android.R.id.home -> {
                        hideKeyboard()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(IMAGE_URI, photoUri)
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