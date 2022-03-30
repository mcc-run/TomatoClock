package com.example.tomatoclock.Countdown

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tomatoclock.Home.Home
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.HttpUtil
import com.example.tomatoclock.TaskList.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import okhttp3.internal.wait
import java.io.IOException
import java.lang.StringBuilder

class Countdown_model : ViewModel() {

    var TotalTime: Long = 25 * 60 * 1000 //总时长
    var mill: Long = 25 * 60 * 1000 //当前剩余时长
    var progress by mutableStateOf(0f) //进度条
    var timeString by mutableStateOf("")    //中间显示的时间条
    var s = StringBuilder() //拼接字符串时使用，最终拼接成时间条
    var minute = 0  //用于onTick函数计算剩余分钟
    var second = 0  //用于onTick函数计算剩余秒
    var ispause by mutableStateOf(false)    //是否暂停
    lateinit var countDownTimer: CountDownTimer    //倒计时
    lateinit var task: Task    //开始倒计时的任务
    var cdown = Cdown()     //    倒计时列表
    var finishtype = mutableStateListOf<Finishtype>()   //    终止原因选择
    var showfinish by mutableStateOf(false)      //    是否显示终止页面
    var showcustom by mutableStateOf(false)      //显示自定义终止原因画面
    var cancelreason by mutableStateOf("")      //自定义取消原因
    var isbreak = false     //是否打断


    //    提交自定义原因
    fun commit_reason() {
        val reason = Finishtype(reason = cancelreason, userid = task.userid)
        val body =
            FormBody.Builder().add("reason", cancelreason).add("userid", task.userid.toString())
                .build()
        HttpUtil.postRequest("addfinishtype", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string().toInt()
                reason.id = data
                finishtype.add(reason)
            }

        })
        showcustom = false

    }

    //    取消提交原因
    fun cancel_reason() {
        showcustom = false
    }

    //提前结束倒计时任务
    fun break_countdown(id: Int) {
        cdown.finishtype = id
        isbreak = true
        showfinish = false
        homeModel.showCountDown = false
        countDownTimer.onFinish()
    }

    //    取消 结束任务
    fun break_cancel() {
        cdown.interrupt += 1
        showfinish = false
        setCountDown(mill)
        countDownTimer.start()
    }

    //获取取消原因列表
    private fun getfinishtype() {
        val body = FormBody.Builder().add("userid", homeModel.user.id.toString()).build()
        HttpUtil.postRequest("getfinishtype", body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val gson = Gson()
                val typeOf = object : TypeToken<List<Finishtype>>() {}.type
                val types = gson.fromJson<List<Finishtype>>(data, typeOf)
                for (type in types) {
                    finishtype.add(type)
                }
                finishtype.removeAt(0)
            }
        })
    }

    //    点击结束按钮打开结束原因页面
    fun stop() {
        countDownTimer.cancel()
        showfinish = true;
    }

    //    设置倒计时项
    fun setCountDown(totaltime: Long) {
        countDownTimer = object : CountDownTimer(totaltime, 1000) {
            //1000ms运行一次onTick里面的方法
            override fun onFinish() {
                mill = mill / 1000 * 1000
                commit_countdown()  //将结束任务提交
                homeModel.showCountDown = false  //关闭倒计时页面
            }

            override fun onTick(millisUntilFinished: Long) {
                mill = millisUntilFinished
                minute = (mill / 1000 / 60).toInt()
                second = (mill / 1000 % 60).toInt()
                if (minute == 0) s.append("00 分 ")
                else if (minute < 10) s.append("0${minute} 分 ")
                else s.append("${minute} 分 ")
                if (second == 0) s.append("00 秒")
                else if (second < 10) s.append("0${second} 秒")
                else s.append("${second} 秒")
                timeString = s.toString()
                s.clear()
                progress = ((TotalTime - millisUntilFinished).toFloat() / TotalTime.toFloat())
            }
        }
    }


    //提交计时任务
    private fun commit_countdown() {
        //                计算专注时间
        val time_count = ((TotalTime - mill) / 1000 / 60).toInt()
        var body = FormBody.Builder().add("userid", cdown.userid.toString())
            .add("tasksetid", cdown.tasksetid.toString())
            .add("time_count", time_count.toString())
            .add("interrupt", cdown.interrupt.toString())
            .add("finishtype", cdown.finishtype.toString())
            .add("taskid", cdown.taskid.toString())
            .build()
        HttpUtil.postRequest("addcountdown", body = body)
        body = FormBody.Builder().add("count", 1.toString())
            .add("time", time_count.toString())
            .add("userid", homeModel.user.id.toString()).build()
        HttpUtil.postRequest("updateaccumulatefocus", body)
        body = FormBody.Builder()
            .add("userid", homeModel.user.id.toString())
            .add("count", if (isbreak) "0" else "1")
            .add("breakcount", if (isbreak) "1" else "0")
            .add("time", time_count.toString()).build()
        HttpUtil.postRequest("updatefocusoftoday", body)
    }

    //开始计时
    fun start(t: Task) {
        task = t
        TotalTime = task.time_count.toLong() * 60 * 1000    //获取总时长
        mill = TotalTime    //设置当前时长
//        初始化当前倒计时实例
        cdown = Cdown()
        cdown.userid = task.userid
        cdown.tasksetid = task.tasksetid
        cdown.taskid = task.id
        //获取结束类型
        finishtype.clear()
        getfinishtype()

        homeModel.showCountDown = true  //将倒计时页面显示出来

//        开始倒计时
        setCountDown(mill)
        countDownTimer.start()
    }

    //暂停或继续
    fun pause_restart() {
        if (ispause) {
            setCountDown(mill)
            countDownTimer.start()
        } else {
            cdown.interrupt += 1
            countDownTimer.cancel()
        }
        ispause = !ispause
    }

}