package com.example.tomatoclock

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.tomatoclock.Home.Home
import com.example.tomatoclock.Home.home_model
import com.example.tomatoclock.My.My_model
import com.example.tomatoclock.Register.User
import com.example.tomatoclock.Register.register_model
import com.example.tomatoclock.Statistics.statistic_model
import com.example.tomatoclock.TaskList.Task
import com.example.tomatoclock.TaskList.tasklist_model
import com.example.tomatoclock.TaskSet.set
import com.example.tomatoclock.TaskSet.taskset_model
import com.example.tomatoclock.ui.theme.TomatoClockTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.File
import java.io.IOException


class MainActivity : ComponentActivity() {

    val takePhoto = 1
    val fromAlbum = 2
    lateinit var imageUri: Uri
    lateinit var outputImage: File

    val tasksetModel = taskset_model()
    val tasklistModel = tasklist_model()
    val statisticModel = statistic_model()
    val registerModel = register_model()
    val myModel = My_model()
    val homeModel = home_model()
//    val countdownModel = Countdown_model()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        获取权限
        getpermissions()

//        查看是否登录,若以登录则获取用户信息
        myModel.imageView = ImageView(this)
        val id = 123456
        if (id != 0){
            getuser(id)
            gettaskset(id)
            gettaskbyid(id)

        }
        // 创建File对象，用于存储拍照后的图片
        savephoto()
        registerModel.imageuri = imageUri
        registerModel.image = ImageView(this)


        setContent {
            TomatoClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Home(tasksetModel,
                        tasklistModel,
                        statisticModel,
                        registerModel,
                        myModel,
                        homeModel)
                }
            }
        }
    }


    private fun gettaskbyid(id: Int) {
        val task_body = FormBody.Builder().add("userid", id.toString()).build()
        HttpUtil.postRequest("gettaskbyuserid", task_body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                val typeOf = object : TypeToken<List<Task>>() {}.type
                val gson = Gson()
                val tasks = gson.fromJson<List<Task>>(data, typeOf)
                for (task in tasks) {
                    tasklistModel.tasks.add(task)
                }
            }

        })
    }

    private fun savephoto() {
        outputImage = File(externalCacheDir, "output_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.example.tomatoclock.fileprovider", outputImage);
        } else {
            Uri.fromFile(outputImage);
        }
    }

    private fun gettaskset(id: Int) {
        val sets_body =
            FormBody.Builder().add("userid", id.toString()).build()
        HttpUtil.postRequest("tasksetbyid", sets_body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body!!.string()
                val gson = Gson()
                val typeOf = object : TypeToken<List<set>>() {}.type
                val sets = gson.fromJson<List<set>>(data, typeOf)
                for (set in sets) {
                    initTaskset(set)
                }

            }

        })
    }

    private fun initTaskset(set: set) {
        val gson = Gson()
        val tasks_body = FormBody.Builder().add("tasksetid", set.id.toString()).build()
        HttpUtil.postRequest("getTaskById", tasks_body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string() ?: ""
                if (data != "") {
                    val typeOf = object : TypeToken<List<Task>>() {}.type
                    val tasks = gson.fromJson<List<Task>>(data, typeOf)
                    set.taskset = tasks as MutableList<Task>
                }
                tasksetModel.sets.add(set)
                tasksetModel.visibles.add(false)
                tasksetModel.rotates.add(0f)
            }

        })
    }

    private fun getuser(id: Int) {
        val user_body =
            FormBody.Builder()
                .add("id", id.toString()).build()
        HttpUtil.postRequest("getuserbyid", body = user_body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                val gson = Gson()
                val user = gson.fromJson(data, User::class.java)
                homeModel.user = user
                homeModel.islogin = true
                if (user.image != "") {
                    val body = FormBody.Builder().add("filename", user.image).build()
                    HttpUtil.postRequest("download", body = body, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val bytes = response.body!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(bytes)
                            myModel.imageView.setImageBitmap(bitmap)
                            myModel.headphoto = true
                        }

                    })
                }
            }
        })
    }

    private fun getpermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                2
            );
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            );
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), 3);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                registerModel.open = false
                registerModel.opencrema = false
                if (resultCode == Activity.RESULT_OK) {
                    // 将拍摄的照片显示出来
                    val bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    registerModel.ischoose = true
                    registerModel.image.setImageBitmap(bitmap)
                    registerModel.bitmap = bitmap

                }
            }
            fromAlbum -> {
                registerModel.open = false
                registerModel.openalbum = false
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 将选择的照片显示
                        val bitmap = getBitmapFromUri(uri)
                        registerModel.ischoose = true
                        registerModel.image.setImageBitmap(bitmap)
                        registerModel.bitmap = bitmap!!
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }


}

