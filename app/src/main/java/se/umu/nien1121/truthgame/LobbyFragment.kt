package se.umu.nien1121.truthgame

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import se.umu.nien1121.truthgame.databinding.FragmentLobbyBinding

/**
 * [Fragment] displaying all registered players and their scores.
 */
class LobbyFragment : Fragment() {

    /**
     * Shared ViewModel owned by [MainActivity]
     */
    private val gameModel: GameViewModel by activityViewModels()

    //ViewBinding
    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    /**
     * Custom adapter for [RecyclerView]
     */
    private var adapter: PlayerAdapter? = PlayerAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        //Set LayoutManager to manage animations of recyclerview
        binding.playerRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSupportActionBar()
        updateUI(gameModel.getPlayers())
        setListeners()
    }

    /**
     * Helper method for configuring the ActionBar, setting the correct title and navigational options
     */
    private fun setSupportActionBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                // Clear current options, set title and add correct menu options
                menu.clear()
                (requireActivity() as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(
                    false
                )
                (requireActivity() as AppCompatActivity).supportActionBar!!.setTitle(R.string.app_name)
                inflater.inflate(R.menu.lobby_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    R.id.clear_lobby -> {
                        gameModel.clear()
                        updateUI(gameModel.getPlayers())
                        true
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Helper method for setting listeners for [View] objects
     */
    private fun setListeners() {
        binding.addPlayerButton.setOnClickListener {
            launchPlayerDetailsFragment(-1)
        }

        binding.startGameButton.setOnClickListener {
            //TODO: Start game
        }
    }

    /**
     * Set's correct ActionBar options/apperance, via [setSupportActionBar]
     */
    override fun onResume() {
        super.onResume()
        setSupportActionBar()
    }

    /**
     * Saves state of [gameModel], via [GameViewModel.saveState]
     */
    override fun onStop() {
        super.onStop()
        gameModel.saveState()
    }

    /**
     * Helper method for updating ui, dependent on current amount of players in [gameModel]
     * @param players: updated list of players provided by [gameModel]
     */
    private fun updateUI(players: List<Player>) {
        if (players.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.playerRecyclerView.visibility = View.GONE
            binding.startGameButton.isEnabled = false
        } else {
            binding.emptyView.visibility = View.GONE
            binding.playerRecyclerView.visibility = View.VISIBLE
            adapter = PlayerAdapter(players)
            binding.playerRecyclerView.adapter = adapter
            binding.startGameButton.isEnabled = players.size >= 2
        }
    }

    /**
     * Helper method for launching a [PlayerDetailsFragment]
     * @param playerIndex: the current index of a already existing player. Is negative if non-existent.
     */
    private fun launchPlayerDetailsFragment(playerIndex: Int) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            PlayerDetailsFragment.newInstance(playerIndex)
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Custom [RecyclerView.Adapter] for use in [RecyclerView] presenting [Player] objects.
     */
    private inner class PlayerAdapter(var players: List<Player>) :
        RecyclerView.Adapter<PlayerHolder>() {

        /**
         * Returns
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
            return PlayerHolder(layoutInflater.inflate(R.layout.list_item_player, parent, false))
        }

        override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
            holder.bind(players[position])
        }

        override fun getItemCount() = players.size

    }

    /**
     * ViewHolder utilized by [PlayerAdapter] in [RecyclerView]. Determines layout of list items
     * and list item behavior when interacted with.
     * @param view: inflated view to present in [RecyclerView].
     */
    private inner class PlayerHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, View.OnLongClickListener {
        private lateinit var player: Player

        //Inite views
        private val nameTextView: TextView = itemView.findViewById(R.id.player_textView)
        private val pictureImageView: ImageView = itemView.findViewById(R.id.imageview_player)
        private val playerCard: CardView = itemView.findViewById(R.id.player_card)
        private val playerScore: TextView = itemView.findViewById(R.id.player_score)

        init {
            //Init listeners
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        /**
         * Binds [Player] object along with properties to player [CardView].
         * @param player: [Player] object for current position
         */
        fun bind(player: Player) {
            this.player = player
            nameTextView.text = this.player.name
            pictureImageView.setImageBitmap(this.player.image)
            playerScore.text = getString(R.string.score, player.score.toString())
            playerCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    player.favouriteColor
                )
            )
        }

        /**
         * Opens [PlayerDetailsFragment] for editing [Player] object.
         */
        override fun onClick(p0: View?) {
            launchPlayerDetailsFragment(adapterPosition)
        }

        /**
         * Presents dialog with option of deleting [Player] object.
         */
        override fun onLongClick(p0: View?): Boolean {
            MaterialAlertDialogBuilder(context!!)
                .setTitle("Delete player")
                .setMessage(
                    "Are you sure you want to delete " + player.name + "?"
                )
                .setNegativeButton("No") { _, _ ->
                    //Do nothing
                }
                .setPositiveButton("Yes") { _, _ ->
                    //Remove player and update list
                    gameModel.removePlayer(player)
                    updateUI(gameModel.getPlayers())
                }
                .show()
            return true
        }
    }

    companion object {
        fun newInstance(): LobbyFragment {
            return LobbyFragment()
        }
    }
}
