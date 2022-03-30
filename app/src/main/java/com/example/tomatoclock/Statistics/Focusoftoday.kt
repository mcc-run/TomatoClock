package com.example.tomatoclock.Statistics

import java.sql.Date

data class Focusoftoday(
    var id: Int = 0,
    var userid: Int = 0,
    var count: Int = 0,
    var time: Int = 0,
//    var date: Long = System.currentTimeMillis(),
    var breakcount: Int = 0
)