package com.example.tomatoclock.Home

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tomatoclock.Countdown.CountdownPage
import com.example.tomatoclock.My.MyPage
import com.example.tomatoclock.My.My_model
import com.example.tomatoclock.Register.RegisterPage
import com.example.tomatoclock.Register.register_model
import com.example.tomatoclock.Statistics.StatisticsPage
import com.example.tomatoclock.Statistics.statistic_model
import com.example.tomatoclock.TaskList.TaskListPage
import com.example.tomatoclock.TaskList.tasklist_model
import com.example.tomatoclock.TaskSet.TaskSetPage
import com.example.tomatoclock.TaskSet.taskset_model
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rengwuxian.wecompose.ui.WeBottomBar
import kotlinx.coroutines.launch

lateinit var homeModel: home_model

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home(
    taskset: taskset_model,
    tasklist: tasklist_model,
    statistic: statistic_model,
    register: register_model,
    my: My_model,
    home: home_model,

    ) {
    homeModel = home
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        if (homeModel.showCountDown) CountdownPage()
        else if (homeModel.showregister) RegisterPage(register)
        else {
            Column {
                val pagerState = rememberPagerState()
                HorizontalPager(
                    count = 4, Modifier.weight(1f),
                    pagerState
                ) { page ->
                    when (page) {
                        0 -> TaskListPage(tasklist)
                        1 -> TaskSetPage(taskset)
                        2 -> StatisticsPage(statistic)
                        3 -> MyPage(my)
                    }
                }
                val scope = rememberCoroutineScope() // 创建 CoroutineScope
                // 不显示 viewModel.selectedTab，改为 pagerState.currentPage
                WeBottomBar(pagerState.currentPage) { page ->
                    // 点击页签后，在协程里翻页
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            }
        }
    }
}