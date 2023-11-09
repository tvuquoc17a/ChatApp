package models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User (
    val uid : String,
    val username : String,
    val profileImageUrl : String
) : Parcelable{
    constructor() : this("","","")
}
