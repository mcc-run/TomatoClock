package com.example.tomatoclock.Statistics

import android.provider.CalendarContract
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.mfchart.PieChartView
import com.example.mfchart.barchart.BarChartView
import com.example.mfchart.linechart.LineChartView
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.HttpUtil
import com.example.tomatoclock.TaskList.calendarPage
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException
import java.sql.Date
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.properties.Delegates

class statistic_model : ViewModel() {

    var accumulatefocus = Accumulatefocus()     //累计专注
    var focusoftoday = Focusoftoday()   //今日专注

    //    累计专注
    var accumulatecount by mutableStateOf(0)    //次数
    var accumulatetime by mutableStateOf("")    //时间
    var accumulatetimeofday by mutableStateOf("")   //日均专注

    //    今日专注
    var todaytime by mutableStateOf("")

    //    饼状图
    lateinit var pieChartView: PieChartView
    val dateFormat = DateFormat.getDateInstance()
    var date by mutableStateOf(dateFormat.format(Calendar.getInstance().time))
    var startdate by mutableStateOf(Calendar.getInstance())
    var enddate by mutableStateOf(Calendar.getInstance())
    var choice by mutableStateOf(1)     //日月年自定义选择按钮
    var task_taskset by mutableStateOf(1)     //任务、任务集选择
    var updatepie by mutableStateOf(0)  //用于刷新饼状图
    var showCalendar by mutableStateOf(false)   //用于自定义日期
    var isstart by mutableStateOf(true)    //用于在自定义日期时，显示开始日期还是结束日期

    //    柱状图：时间段分配
    var isinit_period by mutableStateOf(false)  //是否初始化数据，若未初始化数据则初始化数据
    lateinit var startdate_period: Date     //时间段的起始日期
    lateinit var enddate_period: Date       //时间段的终止日期
    lateinit var barChartView: BarChartView
    var date_period by mutableStateOf("")
    var updatebar by mutableStateOf(false)

    //    月度数据
    var isinit_monthly = false
    lateinit var startdate_monthly: Date     //时间段的起始日期
    lateinit var enddate_monthly: Date       //时间段的终止日期
    var date_monthly by mutableStateOf("")
    var update_monthly by mutableStateOf(false)
    var data_monthly = mutableStateMapOf<Int,Int>()    //绘图时，具体数据
    var maxday_monthly by mutableStateOf(0)     //绘图时，当月的最大天数

    //    年度数据
    var isinit_annual = false
    var year_annual by mutableStateOf(0)
    var date_annual by mutableStateOf("")
    var update_annual by mutableStateOf(false)
    var data_annual = mutableStateMapOf<Int,Int>()    //绘图时，具体数据



    fun initaccumulatefocus() {
        val body = FormBody.Builder().add("userid", homeModel.user.id.toString()).build()
        HttpUtil.postRequest("getaccumulatefocus", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val gson = Gson()
                accumulatefocus = gson.fromJson(data, Accumulatefocus::class.java)
                accumulatecount = accumulatefocus.count
                var hour = accumulatefocus.time / 60
                var minutes = accumulatefocus.time % 60
                accumulatetime = "${hour}小时${minutes}分钟"
                val days =
                    ((System.currentTimeMillis() - homeModel.user.date) / (24L * 60 * 60 * 1000)) + 1
                val differ = accumulatefocus.time / days
                hour = (differ / 60).toInt()
                minutes = (differ % 60).toInt()
                accumulatetimeofday = "${hour}小时${minutes}分钟"
            }

        })
    }

    fun initfocusoftoday() {
        val date = Date(System.currentTimeMillis())
        val body = FormBody.Builder().add("userid", homeModel.user.id.toString()).build()
        HttpUtil.postRequest("getfocusoftodaybydate", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val gson = Gson()
                focusoftoday = gson.fromJson(data, Focusoftoday::class.java)
                val s = StringBuilder()
                val hour = focusoftoday.time / 60
                if (hour != 0) s.append("${hour}小时")
                val minutes = focusoftoday.time % 60
                if (minutes != 0) s.append("${minutes}分钟")
                if (hour == 0 && minutes == 0) s.append("0")
                todaytime = s.toString()
            }

        })
    }

    fun left_Time_distribution() {
        choice = choice - 1
        if (choice == 0) choice = 4
        setTime_distribution(choice)
    }

    fun right_Time_distribution() {
        choice = choice + 1
        if (choice == 5) choice = 1
        setTime_distribution(choice)
    }

    fun onchangetask_taskset(choice: Int) {
        task_taskset = choice
        setTime_distribution(this.choice)
    }

    //    提交自定义时间
    fun commit_customtime() {
        showCalendar = false
        setTime_distribution(4)
    }

    fun show_customtime() {
        showCalendar = true
        choice = 4
    }

    fun setTime_distribution(choice: Int) {
        this.choice = choice
        val (mindate, maxdate) = setdate(choice)
        if (choice == 4){
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = maxdate.time
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-1)
            date = "${dateFormat.format(mindate)}-${dateFormat.format(calendar.time)}"
        }
        else if (choice != 1) date = "${dateFormat.format(mindate)}-${dateFormat.format(maxdate)}"
        else date = "${dateFormat.format(mindate)}"
        val body = FormBody.Builder().add("userid", homeModel.user.id.toString())
            .add("mindate", mindate.toString()).add("maxdate", maxdate.toString()).build()
        HttpUtil.postRequest(
            if (task_taskset == 1) "getcountdownbyuserid" else "getcountdownbytaskset",
            body,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    val data = response.body!!.string()
                    val typeOf = object : TypeToken<List<Tasktime>>() {}.type
                    val gson = Gson()
                    val tasktimes = gson.fromJson<List<Tasktime>>(data, typeOf)
                    var map = mutableStateMapOf<String, Float>()
                    for (tasktime in tasktimes) {
                        map[tasktime.taskname] =
                            map[tasktime.taskname]?.plus(tasktime.time_count) ?: tasktime.time_count
                    }
                    val piedata = ArrayList<PieEntry>()
                    for (m in map) {
                        piedata.add(PieEntry(m.value, m.key))
                    }
                    pieChartView.setData(piedata)
                    updatepie = 5

                }
            })
    }

    fun setPeriod_distribution(count: Int) {
        val calendar = Calendar.getInstance()
//        设置起始日期以及结束日期
        if (!isinit_period) {
            isinit_period = true
            date_period = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
            calendar.set(Calendar.DAY_OF_MONTH, 0)
            startdate_period = Date(calendar.timeInMillis)
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            enddate_period = Date(calendar.timeInMillis)
        } else {
            calendar.timeInMillis = startdate_period.time
//            设置起始日期
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + count)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1)
            startdate_period.time = calendar.timeInMillis
//            设置结束日期
            calendar.timeInMillis = enddate_period.time
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + count)
            enddate_period.time = calendar.timeInMillis
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
//            设置日期字符串
            date_period = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
        }
        val body = FormBody.Builder().add("userid", homeModel.user.id.toString())
            .add("mindate", startdate_period.toString())
            .add("maxdate", enddate_period.toString()).build()
        HttpUtil.postRequest("getperiodoftimes", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val typeOf = object : TypeToken<List<Periodoftime>>() {}.type
                val gson = Gson()
                val periodoftimes = gson.fromJson<List<Periodoftime>>(data, typeOf)
                val yVals1: ArrayList<BarEntry> = ArrayList()
                for (p in periodoftimes) yVals1.add(BarEntry(p.time.toFloat(), p.count.toFloat()))
                barChartView.setData(yVals1)
                updatebar = true
            }

        })
    }

    fun setMonthly_date(count: Int) {
        val calendar = Calendar.getInstance()
//        设置起始日期以及结束日期
        if (!isinit_monthly) {
            isinit_monthly = true
            date_monthly = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
            calendar.set(Calendar.DAY_OF_MONTH, 0)
            startdate_monthly = Date(calendar.timeInMillis)
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            enddate_monthly = Date(calendar.timeInMillis)
        } else {
            calendar.timeInMillis = startdate_monthly.time
//            设置起始日期
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + count)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1)
            startdate_monthly.time = calendar.timeInMillis
//            设置结束日期
            calendar.timeInMillis = enddate_monthly.time
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + count)
            enddate_monthly.time = calendar.timeInMillis
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
//            设置日期字符串
            date_monthly = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"
//            设置绘图时的最大天数
            maxday_monthly = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        val body = FormBody.Builder().add("userid", homeModel.user.id.toString())
            .add("mindate", startdate_monthly.toString())
            .add("maxdate", enddate_monthly.toString()).build()
        HttpUtil.postRequest("getMonthlydata", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val typeOf = object : TypeToken<List<Periodoftime>>() {}.type
                val gson = Gson()
                val periodoftimes = gson.fromJson<List<Periodoftime>>(data, typeOf)
                data_monthly.clear()
                for (p in periodoftimes) data_monthly[p.time] = p.count
                update_monthly = true
            }

        })
    }

    fun setAnnual_date(count: Int) {

//        设置年份
        if (!isinit_annual) {
            val calendar = Calendar.getInstance()
            isinit_annual = true
            year_annual = calendar.get(Calendar.YEAR)
            date_annual = "${year_annual}年"
        } else {
            year_annual += count
            Log.d("setAnnual_date",count.toString())
            date_annual = "${year_annual}年"
        }

        val body = FormBody.Builder().add("userid", homeModel.user.id.toString())
            .add("year", year_annual.toString()).build()
        HttpUtil.postRequest("getAnnualdata", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val typeOf = object : TypeToken<List<Periodoftime>>() {}.type
                val gson = Gson()
                val periodoftimes = gson.fromJson<List<Periodoftime>>(data, typeOf)
                data_annual.clear()
                for (p in periodoftimes) data_annual[p.time] = p.count
                update_annual = true
            }

        })
    }


    private fun setdate(choice: Int): Pair<Date, Date> {
        if (choice == 4) {
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = enddate.timeInMillis
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1)
            return Pair(Date(startdate.timeInMillis), Date(calendar.timeInMillis))
        }
        val calendar = Calendar.getInstance()
        calendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH] + 1, 0, 0] =
            0
        val maxdate = Date(calendar.timeInMillis)
        calendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH] - 1, 0, 0] =
            0
        when (choice) {
            1 -> calendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH], 0, 0] =
                0
            2 -> calendar[calendar[Calendar.YEAR], calendar[Calendar.MONTH], 0, 0, 0] = 0
            3 -> calendar[calendar[Calendar.YEAR], 0, 0, 0, 0] = 0
        }
        val mindate = Date(calendar.timeInMillis)
        return Pair(mindate, maxdate)
    }


}