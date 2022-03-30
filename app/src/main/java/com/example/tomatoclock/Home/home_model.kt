package com.example.tomatoclock.Home

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tomatoclock.Register.User
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

class home_model : ViewModel(){

    var showCountDown by mutableStateOf(false)

    var showregister by mutableStateOf(false)

    lateinit var imageuri : Uri

    var islogin by mutableStateOf(false)

    lateinit var user: User




}

