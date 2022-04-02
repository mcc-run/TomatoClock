package com.example.tomatoclock


import okhttp3.*
import java.io.IOException

object HttpUtil {



    fun postRequest(address: String,body: FormBody, callback: okhttp3.Callback = object : Callback{
        override fun onFailure(call: Call, e: IOException) {}
        override fun onResponse(call: Call, response: Response) {}
    }){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.0.111:8080/${address}")
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun postphoto(address: String,body: RequestBody, callback: okhttp3.Callback){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.0.111:8080/${address}")
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }



}