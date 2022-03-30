package com.example.tomatoclock.Statistics

import android.content.Context
import android.graphics.Color.rgb
import android.widget.CalendarView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.mfchart.PieChartView
import com.example.mfchart.barchart.BarChartView
import com.example.mfchart.linechart.LineChartView
import com.example.tomatoclock.R
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DateFormat
import java.util.*

lateinit var statisticModel: statistic_model
lateinit var context: Context

@Composable
fun StatisticsPage(statistic: statistic_model) {
    statisticModel = statistic
//    初始化累计专注时间
    statisticModel.initaccumulatefocus()
//    初始化今日专注时间
    statisticModel.initfocusoftoday()
    context = LocalContext.current
//    初始化饼状图
    statisticModel.pieChartView = PieChartView(context = context)
    statisticModel.setTime_distribution(statistic.choice)
//    初始化柱状图
    if (!statisticModel.isinit_period) {
        statisticModel.barChartView = BarChartView(context = context)
    }
    statisticModel.setPeriod_distribution(0)
    //    初始化月度数据
    statisticModel.setMonthly_date(0)
//    初始化年度数据
    statisticModel.setAnnual_date(0)

    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        //    标题栏
        statistic_title(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color(rgb(175, 156, 222)))
        )
        //        主界面
        mainview_statistic(
            Modifier
                .fillMaxWidth()
                .weight(9f)
                .background(Color(rgb(175, 156, 222)))
        )
        if (statisticModel.showCalendar) {
            Dialog(onDismissRequest = { /*TODO*/ }) {
                choicedatePage()
            }
        }
    }

}

@Composable
fun choicedatePage() {
    Column(
        Modifier
            .size(400.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color.White)
    ) {
        //            标题栏
        ConstraintLayout(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val (title, commit, cancel) = createRefs()
            Text(text = "请选择开始日期和结束日期", fontSize = 16.sp, modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 5.dp)
            })
            IconButton(
                onClick = { statisticModel.commit_customtime() },
                modifier = Modifier.constrainAs(commit) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(cancel.start, margin = 5.dp)
                }) {
                Icon(painter = painterResource(id = R.drawable.ic_yes), contentDescription = null)
            }
            IconButton(
                onClick = { statisticModel.showCalendar = false },
                modifier = Modifier.constrainAs(cancel) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, margin = 5.dp)

                }) {
                Icon(painter = painterResource(id = R.drawable.ic_no), contentDescription = null)
            }
        }
        if (statisticModel.isstart) {
            //        选择开始日期
            ConstraintLayout(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val (title, toend) = createRefs()
                Text(text = "请选择开始日期", fontSize = 16.sp, modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, margin = 16.dp)
                })
                IconButton(
                    onClick = { statisticModel.isstart = false },
                    modifier = Modifier.constrainAs(toend) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, margin = 16.dp)
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_right),
                        contentDescription = null
                    )
                }
            }
//        开始日期日历
            AndroidView(
                factory = { CalendarView(context) },
                modifier = Modifier.weight(7f),
                update = {
                    it.date = statisticModel.startdate.timeInMillis
                    it.maxDate = statisticModel.enddate.timeInMillis
                    it.setOnDateChangeListener { view, year, month, day ->
                        statisticModel.startdate.set(year, month, day)
                        val dateFormat = DateFormat.getDateTimeInstance()
                    }
                })
        } else {
//            选择结束日期
            ConstraintLayout(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val (title, toend) = createRefs()
                Text(text = "请选择结束日期", fontSize = 16.sp, modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, margin = 16.dp)
                })
                IconButton(
                    onClick = { statisticModel.isstart = true },
                    modifier = Modifier.constrainAs(toend) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, margin = 16.dp)
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_left),
                        contentDescription = null
                    )
                }
            }
//        结束日期日历
            AndroidView(
                factory = { CalendarView(context) },
                modifier = Modifier.weight(7f),
                update = {
                    it.date = statisticModel.enddate.timeInMillis
                    it.minDate = statisticModel.startdate.timeInMillis
                    it.maxDate = System.currentTimeMillis()
                    it.setOnDateChangeListener { view, year, month, day ->
                        statisticModel.enddate.set(year, month, day)
                    }
                })
        }

    }
}

@Composable
fun mainview_statistic(modifier: Modifier) {
    var item = listOf<Int>(1)
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn() {
            items(item) {
                Cumulativefocus(
                    "累计专注",
                    listOf("次数", "时长", "日均时长"),
                    listOf(
                        statisticModel.accumulatecount.toString(),
                        statisticModel.accumulatetime,
                        statisticModel.accumulatetimeofday
                    )
                )
                Column(Modifier.height(10.dp)) {}
                Cumulativefocus(
                    "今日专注",
                    listOf("次数", "时长", "放弃次数"),
                    listOf(
                        statisticModel.focusoftoday.count.toString(),
                        statisticModel.todaytime,
                        statisticModel.focusoftoday.breakcount.toString()
                    )
                )
                Column(Modifier.height(10.dp)) {}
                var modifier_title = Modifier
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .width(370.dp)
                Time_distribution(modifier_title)
                Column(Modifier.height(10.dp)) {}
                period_distribution(modifier_title)
                Column(Modifier.height(10.dp)) {}
                if (!statisticModel.update_monthly) {
                    Monthly_Year_data(modifier_title,
                        "月度数据",
                        statisticModel.date_monthly,
                        object : IAxisValueFormatter {
                            override fun getDecimalDigits(): Int {
                                return 0
                            }

                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return "${value.toInt()}号"
                            }
                        },
                        statisticModel.data_monthly,
                        { statisticModel.setMonthly_date(-1) },
                        { statisticModel.setMonthly_date(1) },
                        statisticModel.maxday_monthly
                    )
                } else statisticModel.update_monthly = false
                Column(Modifier.height(10.dp)) {}
                if (!statisticModel.update_annual) {
                    Monthly_Year_data(modifier_title, "年度数据", statisticModel.date_annual,
                        object : IAxisValueFormatter {
                            override fun getDecimalDigits(): Int {
                                return 0
                            }

                            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                                return "${value.toInt()}月"
                            }
                        },
                        statisticModel.data_annual, { statisticModel.setAnnual_date(-1) },
                        { statisticModel.setAnnual_date(1) }, 12
                    )
                } else statisticModel.update_annual = false
                Column(Modifier.height(10.dp)) {}
            }
        }

    }
}

@Composable
fun Monthly_Year_data(
    modifier: Modifier,
    title: String,
    date: String,
    x: IAxisValueFormatter,
    map: SnapshotStateMap<Int, Int>,
    onleft: () -> Unit,
    onright: () -> Unit,
    maxday: Int
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title_data(title = title, date = date, onleft = { onleft() }, onright = { onright() })
        AndroidView(factory = { LineChartView(it) }, update = {
            var values = mutableListOf<Entry>()
            for (i in 1..maxday) {
                values.add(Entry(i.toFloat(), map[i]?.toFloat() ?: 0f))
            }
            it.setxAxis(x)
            it.setData(values, "月度数据")

        })
    }

}

@Composable
fun period_distribution(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title_data(
            title = "本月专注时段分布",
            date = statisticModel.date_period,
            onleft = { statisticModel.setPeriod_distribution(-1) },
            onright = { statisticModel.setPeriod_distribution(1) })
        if (!statisticModel.updatebar) AndroidView(
            factory = { statisticModel.barChartView },
            update = {})
        else statisticModel.updatebar = false
    }
}

@Composable
fun Time_distribution(modifier: Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        title_data(
            title = "专注时长分布",
            date = statisticModel.date,
            onleft = { statisticModel.left_Time_distribution() },
            onright = { statisticModel.right_Time_distribution() })
        //        任务、任务集按钮
        Row {
            Text(
                text = "任务", modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .background(
                        color = if (statisticModel.task_taskset == 1) Color(
                            rgb(
                                201,
                                233,
                                248
                            )
                        ) else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .border(
                        border = BorderStroke(0.5.dp, Color(rgb(39, 167, 230))),
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .size(40.dp, 20.dp)
                    .clickable { statisticModel.onchangetask_taskset(1) },
                textAlign = TextAlign.Center,
                color = if (statisticModel.task_taskset == 1) Color(rgb(42, 168, 230)) else Color(
                    rgb(204, 234, 249)
                )

            )
            Text(
                text = "任务集", modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .border(
                        border = BorderStroke(0.5.dp, Color(rgb(39, 167, 230))),
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .background(
                        color = if (statisticModel.task_taskset == 2) Color(
                            rgb(
                                201,
                                233,
                                248
                            )
                        ) else Color.White
                    )
                    .size(50.dp, 20.dp)
                    .clickable { statisticModel.onchangetask_taskset(2) },
                textAlign = TextAlign.Center,
                color = if (statisticModel.task_taskset == 2) Color(rgb(42, 168, 230)) else Color(
                    rgb(204, 234, 249)
                )
            )
        }
        Column(Modifier.height(10.dp)) {}
//        日月年按钮
        Row {
            Text(
                text = "日", modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .background(
                        color = if (statisticModel.choice == 1) Color(
                            rgb(
                                201,
                                233,
                                248
                            )
                        ) else Color.White,
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .border(
                        border = BorderStroke(0.5.dp, Color(rgb(39, 167, 230))),
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp
                        )
                    )
                    .size(40.dp, 20.dp)
                    .clickable { statisticModel.setTime_distribution(1) },
                textAlign = TextAlign.Center,
                color = if (statisticModel.choice == 1) Color(rgb(42, 168, 230)) else Color(
                    rgb(
                        204,
                        234,
                        249
                    )
                )

            )
            Text(
                text = "月", modifier = Modifier
                    .background(
                        color = if (statisticModel.choice == 2) Color(
                            rgb(
                                201,
                                233,
                                248
                            )
                        ) else Color.White
                    )
                    .border(
                        border = BorderStroke(0.5.dp, Color(rgb(39, 167, 230)))
                    )
                    .size(40.dp, 20.dp)
                    .clickable { statisticModel.setTime_distribution(2) },
                textAlign = TextAlign.Center,
                color = if (statisticModel.choice == 2) Color(rgb(42, 168, 230)) else Color(
                    rgb(
                        204,
                        234,
                        249
                    )
                )
            )
            Text(
                text = "年", modifier = Modifier
                    .border(
                        border = BorderStroke(0.5.dp, Color(rgb(39, 167, 230)))
                    )
                    .background(
                        color = if (statisticModel.choice == 3) Color(
                            rgb(
                                201,
                                233,
                                248
                            )
                        ) else Color.White
                    )
                    .size(40.dp, 20.dp)
                    .clickable { statisticModel.setTime_distribution(3) },
                textAlign = TextAlign.Center,
                color = if (statisticModel.choice == 3) Color(rgb(42, 168, 230)) else Color(
                    rgb(
                        204,
                        234,
                        249
                    )
                )
            )
            Text(
                text = "自定", modifier = Modifier
                    .clip(
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .border(
                        border = BorderStroke(0.5.dp, Color(rgb(39, 167, 230))),
                        shape = RoundedCornerShape(
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .background(
                        color = if (statisticModel.choice == 4) Color(
                            rgb(
                                201,
                                233,
                                248
                            )
                        ) else Color.White
                    )
                    .size(50.dp, 20.dp)
                    .clickable { statisticModel.show_customtime() },
                textAlign = TextAlign.Center,
                color = if (statisticModel.choice == 4) Color(rgb(42, 168, 230)) else Color(
                    rgb(
                        204,
                        234,
                        249
                    )
                )
            )
        }

        if (statisticModel.updatepie == 0) AndroidView(factory = { statisticModel.pieChartView })
        if (statisticModel.updatepie == 5) statisticModel.updatepie = 0
    }
}

@Composable
fun title_data(
    title: String,
    date: String,
    onleft: () -> Unit,
    onright: () -> Unit,
) {
    ConstraintLayout(
        Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {
        val (c_title, c_date, c_left, c_right) = createRefs()
        Text(
            text = title,
            fontSize = 15.sp,
            color = Color(rgb(38, 167, 230)),
            modifier = Modifier.constrainAs(c_title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, 10.dp)
            })
        Text(
            text = date,
            fontSize = 12.sp,
            color = Color(rgb(38, 167, 230)),
            modifier = Modifier.constrainAs(c_date) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(c_title.end, 10.dp)
            })
        IconButton(onClick = { onleft() }, modifier = Modifier.constrainAs(c_left) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(c_right.end, 30.dp)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_left), contentDescription = null)
        }
        IconButton(onClick = { onright() }, modifier = Modifier.constrainAs(c_right) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, 10.dp)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_right), contentDescription = null)
        }
    }
}

//累计专注、今日专注页面
@Composable
fun Cumulativefocus(title: String, smalltitle: List<String>, message: List<String>) {
    Column(
        Modifier
            .size(370.dp, 90.dp)
            .background(Color.White, shape = RoundedCornerShape(20.dp))
    ) {
        ConstraintLayout(Modifier.weight(2f)) {
            val text = createRef()
            Text(text = title, Modifier.constrainAs(text) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 10.dp)
            }, fontSize = 15.sp, color = Color(rgb(38, 167, 230)))
        }
        Row(
            Modifier
                .weight(8f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            showmessage(title = smalltitle[0], message = message[0], 20)
            showmessage(title = smalltitle[1], message = message[1], 20)
            showmessage(title = smalltitle[2], message = message[2], 0)
        }
    }
}

@Composable
fun showmessage(title: String, message: String, padding: Int) {
    Column(Modifier.padding(end = padding.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = Color(rgb(38, 167, 230)))
        Text(text = message, color = Color(rgb(38, 167, 230)))
    }
}


//标题栏
@Composable
fun statistic_title(modifier: Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (title) = createRefs()
//        标题
        Text(text = "统计数据", Modifier.constrainAs(title) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, fontSize = 25.sp, color = Color.White)
    }
}