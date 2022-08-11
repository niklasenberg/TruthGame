package se.umu.nien1121.truthgame

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

/**
 * Model class for a player, containing their picture, name, favourite colour and current score.
 * Implements [Parcelable] in order to persist through [GameViewModel.saveState].
 */
data class Player(
    var image: Bitmap,
    var name: String,
    var favouriteColor: Int,
    var score: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Bitmap::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(image, flags)
        parcel.writeString(name)
        parcel.writeInt(favouriteColor)
        parcel.writeInt(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}