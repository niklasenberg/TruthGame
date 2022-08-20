package se.umu.nien1121.truthgame.database

import androidx.room.TypeConverter
import java.util.*

/**
 * Type conversion of entity properties, handled by Room database
 */
class QuestionTypeConverters {

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}