package se.umu.nien1121.truthgame

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

//Constants
private const val PLAYER_LIST_KEY = "se.umu.nien1121.playerList"

/**
 * Shared [ViewModel], owned by [MainActivity]. Handles registered [Player] objects.
 * @param handle: SavedStateHandle used by constructor to reset prior state upon destruction of ViewModel
 */
class GameViewModel(private val handle: SavedStateHandle) : ViewModel() {
    /**
     * List of [Player] objects
     */
    private var playerList = mutableListOf<Player>()

    //Load players from handle, if any
    init {
        playerList = handle[PLAYER_LIST_KEY] ?: mutableListOf()
    }

    /**
     * Removes all [Player] objects from game.
     */
    fun clear() {
        playerList.clear()
    }

    /**
     * Removes [Player] object from game.
     * @param player: player to be removed
     */
    fun removePlayer(player: Player) {
        playerList.remove(player)
    }

    /**
     * Primary method used for saving [Player] information.
     * @param playerIndex: position of [Player] in [playerList]. Is negative if non-existent.
     * @param imageUri: player's captured image to be saved
     * @param name: name of player to be saved
     * @param favouriteColor: player's favorite colour to be saved
     */
    fun savePlayer(playerIndex: Int, imageUri: Uri, name: String, favouriteColor: Int) {
        if (playerIndex >= 0) {
            //If player exists, update it
            updatePlayer(playerIndex, imageUri, name, favouriteColor)
        } else {
            //Else add new player object
            addPlayer(imageUri, name, favouriteColor)
        }
    }

    /**
     * Helper method for adding a new [Player] to [playerList]. Called by [savePlayer].
     * @param imageUri: new player's captured image
     * @param name: name of new player
     * @param favouriteColor: new player's favorite colour
     */
    private fun addPlayer(imageUri: Uri, name: String, favouriteColor: Int) {
        val player = Player(imageUri = imageUri, name = name, favouriteColor = favouriteColor)
        playerList.add(player)
    }

    /**
     * Helper method for updating a [Player] already in [playerList]. Called by [savePlayer].
     * @param playerIndex: position of [Player] in [playerList].
     * @param imageUri: player's captured image to be saved
     * @param name: name of player to be saved
     * @param favouriteColor: player's favorite colour to be saved
     */
    private fun updatePlayer(playerIndex: Int, imageUri: Uri, name: String, favouriteColor: Int) {
        val player = playerList[playerIndex]
        player.imageUri = imageUri
        player.name = name
        player.favouriteColor = favouriteColor
    }

    //Getters
    fun getPlayers(): List<Player> {
        return playerList
    }

    fun getPlayerImage(i: Int): Uri {
        return playerList[i].imageUri
    }

    fun getPlayerName(i: Int): String {
        return playerList[i].name
    }

    fun getPlayerColor(i: Int): Int {
        return playerList[i].favouriteColor
    }

    /**
     * Saves current game state in [handle]. Needs to be called in Fragments or Activities onStop().
     */
    fun saveState() {
        handle[PLAYER_LIST_KEY] = playerList
    }
}