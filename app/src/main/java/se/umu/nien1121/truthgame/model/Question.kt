package se.umu.nien1121.truthgame.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Model class for questions stored in database.
 * @param id: Auto generated ID for specific object
 * @param content: the question itself
 */
@Entity
data class Question(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val content: String = "",
)