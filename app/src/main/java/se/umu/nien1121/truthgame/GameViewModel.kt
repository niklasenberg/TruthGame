package se.umu.nien1121.truthgame

import android.graphics.Bitmap
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
     * @param image: player's captured image to be saved
     * @param name: name of player to be saved
     * @param favouriteColor: player's favorite colour to be saved
     */
    fun savePlayer(playerIndex: Int, image: Bitmap, name: String, favouriteColor: Int) {
        if (playerIndex >= 0) {
            //If player exists, update it
            updatePlayer(playerIndex, image, name, favouriteColor)
        } else {
            //Else add new player object
            addPlayer(image, name, favouriteColor)
        }
    }

    /**
     * Helper method for adding a new [Player] to [playerList]. Called by [savePlayer].
     * @param image: new player's captured image
     * @param name: name of new player
     * @param favouriteColor: new player's favorite colour
     */
    private fun addPlayer(image: Bitmap, name: String, favouriteColor: Int) {
        val player = Player(image = image, name = name, favouriteColor = favouriteColor)
        playerList.add(player)
    }

    /**
     * Helper method for updating a [Player] already in [playerList]. Called by [savePlayer].
     * @param playerIndex: position of [Player] in [playerList].
     * @param image: player's captured image to be saved
     * @param name: name of player to be saved
     * @param favouriteColor: player's favorite colour to be saved
     */
    private fun updatePlayer(playerIndex: Int, image: Bitmap, name: String, favouriteColor: Int) {
        val player = playerList[playerIndex]
        player.image = image
        player.name = name
        player.favouriteColor = favouriteColor
    }

    //Getters
    fun getPlayers(): List<Player> {
        return playerList
    }

    fun getPlayerImage(i: Int): Bitmap {
        return playerList[i].image
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