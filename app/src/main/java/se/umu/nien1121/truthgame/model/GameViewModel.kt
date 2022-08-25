package se.umu.nien1121.truthgame.model

import android.net.Uri
import androidx.lifecycle.*
import java.util.*

//Constants
private const val PLAYER_LIST_KEY = "se.umu.nien1121.playerList"
private const val CURRENT_PLAYER_KEY = "se.umu.nien1121.currentPlayer"

/**
 * Shared [ViewModel], owned by [se.umu.nien1121.truthgame.controller.MainActivity]. Handles registered [Player] objects.
 * @param handle: SavedStateHandle used by constructor to reset prior state upon destruction of ViewModel
 */
class GameViewModel(private val handle: SavedStateHandle) : ViewModel() {
    /**
     * List of registered [Player] objects
     */
    private var playerList = mutableListOf<Player>()

    /**
     * Index of player with current turn
     */
    private var currentPlayerIndex = 0

    //Repository
    private var questionRepository: QuestionRepository = QuestionRepository.get()

    //LiveData to be observed
    private val randomQuestionLiveData = MutableLiveData<UUID>()
    var questionLiveData: LiveData<Question> = Transformations.switchMap(randomQuestionLiveData) {
        questionRepository.getRandomQuestion()
    }

    //Load players from savedstatehandle, if any
    init {
        playerList = handle[PLAYER_LIST_KEY] ?: mutableListOf()
        currentPlayerIndex = handle[CURRENT_PLAYER_KEY] ?: 0
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

    /**
     * Helper method for advancing game state, noting score and supplying new player/question.
     * @param answer: Whether the answer was found to be true or not
     */
    fun nextRound(answer: Boolean) {
        //If true, increase player score
        if (answer) {
            getCurrentPlayer().score += 1
        }

        //Go to next player, loop if at end of list
        currentPlayerIndex += 1
        if (currentPlayerIndex == playerList.size) {
            currentPlayerIndex = 0
        }

        //Mutate LiveData and supply new question
        getNewQuestion()
    }

    fun clear() {
        playerList.clear()
    }

    //Getters
    fun getPlayers(): List<Player> {
        return playerList
    }

    fun getPlayer(i: Int): Player {
        return playerList[i]
    }

    fun getCurrentPlayer(): Player {
        return playerList[currentPlayerIndex]
    }

    /**
     * Mutates LiveData which triggers refresh from observers of [questionLiveData]
     */
    fun getNewQuestion() {
        randomQuestionLiveData.value = UUID.randomUUID()
    }

    /**
     * Saves current game state in [handle]. Needs to be called in Fragments or Activities onStop().
     */
    fun saveState() {
        handle[PLAYER_LIST_KEY] = playerList
        handle[CURRENT_PLAYER_KEY] = currentPlayerIndex
    }
}