package com.example.braillekeyboard

import android.os.Parcel
import android.os.Parcelable

data class ItemDictionary(
    val title: String,
    val imageResId: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(imageResId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDictionary> {
        override fun createFromParcel(parcel: Parcel): ItemDictionary {
            return ItemDictionary(parcel)
        }

        override fun newArray(size: Int): Array<ItemDictionary?> {
            return arrayOfNulls(size)
        }
    }
}
