package se.umu.nien1121.truthgame.controller

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
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.FragmentLobbyBinding
import se.umu.nien1121.truthgame.model.GameViewModel
import se.umu.nien1121.truthgame.model.Player
import se.umu.nien1121.truthgame.setPicture

/**
 * [Fragment] displaying all registered players and their scores.
 */
class LobbyFragment : Fragment() {

    /**
     * Shared ViewModel owned by [MainActivity]
     */
    private val gameViewModel: GameViewModel by activityViewModels()

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
        setLobbyActionBar()
        updateUI(gameViewModel.getPlayers())
        setListeners()
    }

    /**
     * Custom helper method for configuring the ActionBar, setting the correct title, navigational options and menu items
     */
    private fun setLobbyActionBar() {
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
                    R.id.my_questions -> {
                        launchFragment(QuestionListFragment.newInstance())
                        true
                    }
                    R.id.clear_lobby -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Clear lobby")
                            .setMessage(
                                "Are you sure you want to clear lobby?"
                            )
                            .setNegativeButton("No") { _, _ ->
                                //Do nothing
                            }
                            .setPositiveButton("Yes") { _, _ ->
                                gameViewModel.clear()
                                updateUI(gameViewModel.getPlayers())
                            }
                            .show()
                        true
                    }
                    R.id.show_rules -> {
                        launchFragment(RulesFragment.newInstance())
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
            launchFragment(PlayerDetailsFragment.newInstance(-1))
        }

        binding.startGameButton.setOnClickListener {
            launchFragment(GameFragment.newInstance())
        }
    }

    /**
     * Set's correct ActionBar options/apperance, via [setLobbyActionBar]
     */
    override fun onResume() {
        super.onResume()
        setLobbyActionBar()
    }

    /**
     * Saves state of [gameViewModel], via [GameViewModel.saveState]
     */
    override fun onStop() {
        super.onStop()
        gameViewModel.saveState()
    }

    /**
     * Helper method for updating ui, dependent on current amount of players in [gameViewModel]
     * @param players: updated list of players provided by [gameViewModel]
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

    private fun launchFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            fragment
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Custom [RecyclerView.Adapter] for use in [RecyclerView] presenting [Player] objects.
     */
    private inner class PlayerAdapter(var players: List<Player>) :
        RecyclerView.Adapter<PlayerHolder>() {

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
            setPicture(pictureImageView, this.player.imageUri, requireContext())
            playerScore.text = getString(R.string.score, player.score.toString())
            playerCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    player.favouriteColor
                )
            )
        }

        /**
         * Opens [PlayerDetailsFragment] for editing [Player] object.
         */
        override fun onClick(p0: View?) {
            launchFragment(PlayerDetailsFragment.newInstance(adapterPosition))
        }

        /**
         * Presents dialog with option of deleting [Player] object.
         */
        override fun onLongClick(p0: View?): Boolean {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete player")
                .setMessage(
                    "Are you sure you want to delete " + player.name + "?"
                )
                .setNegativeButton("No") { _, _ ->
                    //Do nothing
                }
                .setPositiveButton("Yes") { _, _ ->
                    //Remove player and update list
                    gameViewModel.removePlayer(player)
                    updateUI(gameViewModel.getPlayers())
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
