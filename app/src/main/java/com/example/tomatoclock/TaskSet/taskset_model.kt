package com.example.tomatoclock.TaskSet

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.HttpUtil
import com.example.tomatoclock.TaskList.Task
import com.example.tomatoclock.TaskList.tasklistModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

class taskset_model : ViewModel() {

    //    动画可见
    var visibles = mutableStateListOf<Boolean>()
    var rotates = mutableStateListOf<Float>()
    var refresh = mutableStateListOf<Boolean>()

    fun unfold(index: Int) {
        visibles[index] = !visibles[index]
        if (rotates[index] == 0f) rotates[index] = 90f
        else rotates[index] = 0f
    }

    //    任务集
    var sets = mutableStateListOf<set>()
    var addset by mutableStateOf(false)

    var setname by mutableStateOf("")

    var error by mutableStateOf("")
    var showerror by mutableStateOf(false)

    fun oncommit_sets() {
        if (setname == "") {
            error = "集合名不能为空!"
            showerror = true
            Log.d("oncommit_sets", "true")
            return
        }
        for (s in sets) {
            if (setname == s.name) {
                error = "集合名不能重复"
                showerror = true
                Log.d("oncommit_sets", "true")
                return
            }
        }
        var set = set()
        set.name = setname
        setname = ""
        sets.add(set)
        val body =
            FormBody.Builder().add("userid", homeModel.user.id.toString()).add("name", set.name)
                .build()
        HttpUtil.postRequest("addsets", body = body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {}
        })
        visibles.add(false)
        rotates.add(0f)
        refresh.add(false)
        addset = false
    }

    //    添加任务按钮
    var addtask by mutableStateOf(false)

    //
    var addtask_setsindex by mutableStateOf(0)

    //    打开添加任务界面
    fun addtasks(index: Int) {
        addtask_setsindex = index
        addtask = true
    }

    //    提交任务按钮
    fun gettask() {
        var temp = sets[addtask_setsindex]
        var task = tasklistModel.oncommit_add_nonlocal(temp.id)
        sets.remove(temp)
        temp.taskset.add(task)
        sets.add(addtask_setsindex, temp)
        addtask = false
    }

    //    将任务提交至任务列表
    fun commit_tasklist(task: Task) {
        task.userid = homeModel.user.id
        tasklistModel.commit_task(task)
    }

}
