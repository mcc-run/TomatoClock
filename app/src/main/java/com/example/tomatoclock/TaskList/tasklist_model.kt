package com.example.tomatoclock.TaskList

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class tasklist_model : ViewModel() {

    var tasks = mutableStateListOf<Task>()

    var timecount by mutableStateOf(25f)

    //    添加任务按钮
    var addtask by mutableStateOf(false)
    fun onchange_add() {
        addtask = true
    }

    fun oncancell_add() {
        addtask = false

    }

//    开始任务界面

    fun oncommit_add_nonlocal(id: Int): Task{
        var task = Task()
        oncommit_add(task = task,false)
        task.tasksetid = id
        task.userid = -1
        commit_task(task = task)
        return task
    }

    fun oncommit_add(task: Task = Task(),islocal : Boolean = true) {
        if (taskname == null || taskname == ""){
            error = "请输入任务名！"
            showerror = true
            return
        }

        task.userid = homeModel.user.id
        task.taskname = taskname
        task.model_selected = model_selected
        task.time_selected = time_selected
        if(time_selected == 1){
            task.time_count = timecount.toInt()
            timecount = 25f
        }
        if(model_selected == 2){
            task.target_year = year
            task.target_month = month
            task.target_day = day
            task.hour_count = count_time
        }
        if(model_selected == 3){
            task.habit_selected =habit_selected
            task.hour_count =count_habit
        }
        if (islocal){
            commit_task(task)

        }
        addtask = false
        taskname = ""
        model_selected = 1
        time_selected = 1
        times_selected = 1

    }

    public fun commit_task(task: Task) {
        val body = FormBody.Builder()
            .add("userid", task.userid.toString())
            .add("tasksetid", task.tasksetid.toString())
            .add("userid", task.userid.toString())
            .add("taskname", task.taskname)
            .add("model_selected", task.model_selected.toString())
            .add("time_selected", task.time_selected.toString())
            .add("time_count", task.time_count.toString())
            .add("target_year", task.target_year.toString())
            .add("target_month", task.target_month.toString())
            .add("target_day", task.target_day.toString())
            .add("hour_count", task.hour_count.toString())
            .add("habit_selected", task.habit_selected.toString())
            .build()
        HttpUtil.postRequest("addtask", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                task.id = data.toInt()
                tasklistModel.tasks.add(task)
            }

        })
    }

    var showerror by mutableStateOf(false)
    var error by mutableStateOf("")

    //    dialog详细信息
//    任务名
    var taskname by mutableStateOf("")


    //    模式选择
    val modelname = mutableStateListOf<String>("普通番茄钟", "定目标", "养习惯")
    var model_selected by mutableStateOf(1)
    fun getmodelname(index: Int): String {
        return modelname.get(index)
    }

    fun getmodel_selected(index: Int): Boolean {
        return model_selected == index
    }

    fun onchange_model_selected(index: Int) {
        model_selected = index
    }

    //    计时模式
    val timename = mutableStateListOf<String>("倒计时", "正向计时", "不计时")
    var time_selected by mutableStateOf(1)
    fun gettimename(index: Int): String {
        return timename.get(index)
    }

    fun gettime_selected(index: Int): Boolean {
        return time_selected == index
    }

    fun onchange_time_selected(index: Int) {
        time_selected = index
    }

//    时间细节



    var times_selected by mutableStateOf(1)


    var showcalendar by mutableStateOf(false)
    fun opencalendar() {
        showcalendar = true;
    }

    fun closecalendar() {
        showcalendar = false
    }



    //    目标卡细节
//    日期
    var year by mutableStateOf(0)
    var month by mutableStateOf(0)
    var day by mutableStateOf(0)
    var date by mutableStateOf("")
    val mindate = Date()
    fun setyear(y: Int) {
        year = y
        date = "${year}年${month}月${day}日"
        Log.d("year", date)
    }

    fun setmonth(m: Int) {
        month = m
        date = "${year}年${month}月${day}日"
        Log.d("month", date)
    }

    fun setday(d: Int) {
        day = d
        date = "${year}年${month}月${day}日"
        Log.d("day", date)
    }

    //    时间量

    var count_time by mutableStateOf(0)
    fun onchange_count_time(temp: String) {

        var s = StringBuilder()
        if(temp.length>0){
            for (c in temp) {
                if (c.compareTo('0') >= 0 && c.compareTo('9') <= 0) s.append(c)
            }
            count_time = s.toString().substring(0, if (s.length >= 9) 9 else s.length).toInt()
        }
        else count_time = 0

//        计算当前日期与选定日期相差的小时数

        val dateFormat = SimpleDateFormat("HH")
        val c1 = Calendar.getInstance()
        c1.set(year,month-1,day,24,0,0)
        val c2 = Calendar.getInstance()
        c2.set(c2.get(Calendar.YEAR),c2.get(Calendar.MONTH),c2.get(Calendar.DAY_OF_MONTH),24,0,0)
        var differ = c1.timeInMillis - c2.timeInMillis
        differ /= (60 * 60 * 1000).toLong()
        val date = Date()
        val hour: Int = (24 - dateFormat.format(date).toInt() - 1) + differ.toInt()
//        若设置的小时数大于日期中小时数，则时间数=日期中的小时数
        if (count_time > hour) count_time = hour.toInt()
    }


    //  习惯卡细节
    var habit by mutableStateOf("每天")
    var open_select by mutableStateOf(false)
    var habit_selected by mutableStateOf(1)
    fun onchange_habit_selected(selected : Int){
        habit_selected = selected
        open_select = false
        when(selected){
            1 -> habit = "每天"
            2 -> habit = "每周"
            3 -> habit = "每月"
        }
    }
    var count_habit by mutableStateOf(0)
    fun onchange_count_habit(temp: String){
        val day = 24
        val week = 7*day
        val month = 28*week

        var s = StringBuilder()
        if(temp.length>0){
            for (c in temp) {
                if (c.compareTo('0') >= 0 && c.compareTo('9') <= 0) s.append(c)
            }
            count_habit = s.toString().toInt()
        }
        else count_habit = 0

        if (habit_selected == 1 && count_habit > day)count_habit = day
        if (habit_selected == 2 && count_habit > week)count_habit = week
        if (habit_selected == 3 && count_habit > month)count_habit = month

    }

    init {
        var temp = Calendar.getInstance()
        year = temp.get(Calendar.YEAR)
        month = temp.get(Calendar.MONTH) + 1
        day = temp.get(Calendar.DAY_OF_MONTH)
        date = "${year}年${month}月${day}日"
    }
}