package com.example.tomatoclock.Register


import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.HttpUtil
import com.example.tomatoclock.My.myModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.*


class register_model : ViewModel() {

    var open by mutableStateOf(false)

    var opencrema by mutableStateOf(false)

    var openalbum by mutableStateOf(false)

    lateinit var imageuri: Uri

    lateinit var image: ImageView

    lateinit var bitmap: Bitmap

    var ischoose by mutableStateOf(false)

    var name by mutableStateOf("")

    var password by mutableStateOf("")

    var confirmpass by mutableStateOf("")

    var email by mutableStateOf("")

    var problem by mutableStateOf("")

    var answer by mutableStateOf("")



    fun onchangename(name: String) {
        this.name = name
    }

    fun onchangepassword(password: String) {
        this.password = password
    }

    fun onchangeconfirmpass(confirmpass: String) {
        this.confirmpass = confirmpass
    }

    fun onchangeemail(email: String) {
        this.email = email
    }

    fun onchangeproblem(problem: String) {
        this.problem = problem
    }

    fun onchangeanswer(answer: String) {
        this.answer = answer
    }

    var error by mutableStateOf(false)
    var errormessage by mutableStateOf("")

    fun onregister() {
        when {
            name.length > 20 -> errormessage = "昵称长度应在20字内"
            name.length == 0 -> errormessage = "昵称不能为空"
            password.length > 20 -> errormessage = "密码长度应在20字内"
            password != confirmpass -> errormessage = "两次输入的密码不同"
            password.length < 8 -> errormessage = "密码长度应大于8"
            problem.length > 20 -> errormessage = "问题长度应在20字内"
            problem.length == 0 -> errormessage = "问题不能为空"
            answer.length > 20 -> errormessage = "答案长度应在20字内"
            answer.length == 0 -> errormessage = "答案不能为空"
            else -> errormessage = ""
        }
        if (errormessage.length > 0) error = true
        else commit()
    }

    val client = OkHttpClient()

    fun commit() {
        val body =
            FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("email", email)
                .add("problem", problem)
                .add("answer", answer).build()
        HttpUtil.postRequest("User_registration", body = body, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("commit", "发送失败")
            }

            override fun onResponse(call: Call, response: Response) {
                val id = response.body?.string()
                errormessage = "您的账号是：${id}"
                error = true
                homeModel.showregister = false
                myModel.showlogin = false
                if (register.ischoose){
                    val MEDIA_TYPE_PNG : MediaType? = "image/jpg".toMediaTypeOrNull()
                    val fileBody: RequestBody =
                        bitmaptofile(bitmap = bitmap)?.let {
                            RequestBody.create(MEDIA_TYPE_PNG,
                                it
                            )
                        }!!
                    val requestBody: RequestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "${id}.jpg", fileBody)
                        .addFormDataPart("id", id.toString())
                        .build()
                    HttpUtil.postphoto("uploadFile",requestBody,object : Callback{
                        override fun onFailure(call: Call, e: IOException) {
                        }

                        override fun onResponse(call: Call, response: Response) {

                        }
                    })
                }
            }
        })
    }



    fun bitmaptofile(bitmap: Bitmap): File? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        //图片名
        val filename: String = "upload"
        val file = File(
            Environment.getExternalStorageDirectory(),
            "$filename.jpg"
        )
        try {
            val fos = FileOutputStream(file)
            try {
                fos.write(baos.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // recycleBitmap(bitmap);
        return file
    }
}