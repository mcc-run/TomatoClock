package com.example.tomatoclock.Home

import androidx.annotation.DrawableRes
import com.example.tomatoclock.R

enum class bottom(val label : String, @DrawableRes val icon : Int) {

    TaskList("TaskList", R.drawable.ic_tasklist),
    TaskSet("TaskSet",R.drawable.ic_taskset),
    Statistics("Statistics",R.drawable.ic_statistics),
    My("My",R.drawable.ic_my)

}