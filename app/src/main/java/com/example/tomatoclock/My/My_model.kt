package com.example.tomatoclock.My

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.HttpUtil
import com.example.tomatoclock.Register.User
import com.example.tomatoclock.TaskList.erroemesssage
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class My_model : ViewModel() {

    fun login_detail(){
        if (!homeModel.islogin){
            showlogin = true
        }else {

        }
    }

    var showlogin by mutableStateOf(false)

    var userid by mutableStateOf("")
    fun onchangeid(id : String){userid = id}
    var password by mutableStateOf("")
    fun onchangepassword(pass : String){password = pass}

    var showerror by mutableStateOf(false)
    var errormessage by mutableStateOf("")

    var user: User = User()

    lateinit var imageView : ImageView

    var headphoto by mutableStateOf(false)


    fun onregister(){
        myModel.showlogin = false
        homeModel.showregister = true
    }
    fun onlogin(){
        val body =
            FormBody.Builder()
                .add("id", userid).build()
        HttpUtil.postRequest("getuserbyid", body = body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                val gson = Gson()
                user = gson.fromJson(data.toString(),User::class.java)
                //        账户不存在
                if(user.id == 0){
                    myModel.errormessage = "账户不存在"
                    myModel.showerror = true
                }else if(user.password != password){
//            密码错误
                    myModel.errormessage = "密码错误"
                    myModel.showerror = true
                }
                else{
                    homeModel.user = user
                    homeModel.islogin = true
                    myModel.showlogin = false
                    Log.d("onlogin1",user.image)
                    if (user.image != ""){
                        val body = FormBody.Builder().add("filename",user.image).build()
                        HttpUtil.postRequest("download", body = body,object : Callback {
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
            }
        })
    }



}