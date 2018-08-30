package com.example.dilyanayankova.my_messenger.Models

import android.os.Parcelable
import android.text.Editable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImgUrl: String, val password: String, val email: String) : Parcelable {
    //Parcelable to be able to parce it in putExtra
    constructor() : this("", "", "", "", "") //empty constructor not to crash
}
