package com.example.tomatoclock.Register

import android.provider.ContactsContract
import java.sql.Date
import java.sql.Struct
import java.util.*

data class User(
    var id: Int = 0,
    var name: String = "",
    var password: String = "",
    var email: String = "",
    var date: Long = Calendar.getInstance().timeInMillis,
    var problem: String = "",
    var answer: String = "",
    var image : String = ""
){}