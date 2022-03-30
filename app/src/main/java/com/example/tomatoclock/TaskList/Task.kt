package com.example.tomatoclock.TaskList

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var id : Int = -1,
    var tasksetid : Int = -1,
    var userid : Int = -1,
    var taskname: String = "",
    var model_selected: Int = 1,
    var time_selected: Int = 1,
    var time_count: Int = 0,
    var target_year: Int = 0,
    var target_month: Int = 0,
    var target_day: Int = 0,
    var hour_count: Int = 0,
    var habit_selected: Int = 0,
    var effectivedate : Long = 0
) {

//    任务名


//    模式选择


//    计时选择


    //    具体时间


    //    日期


    //    时间量


    //    养习惯





}