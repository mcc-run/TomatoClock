package com.example.tomatoclock.My

import android.graphics.Color.rgb
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.tomatoclock.Home.homeModel
import com.example.tomatoclock.R
import com.example.tomatoclock.Register.item
import com.example.tomatoclock.Register.show_errormessage

var myModel = My_model()

@Composable
fun MyPage(my: My_model) {
    myModel = my
    Column(Modifier.fillMaxSize()) {
        if (myModel.showlogin) LoginPage()
        else Surface(
            elevation = 3.dp, modifier = Modifier.padding(top = 30.dp)
        ) {
            User()
        }
    }
}

@Composable
fun LoginPage() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(
            label = "账号",
            message = myModel.userid,
            valuelistener = { myModel.onchangeid(it) },
            hint = "请输入您的id",
            modifier = Modifier.padding(bottom = 10.dp),
            type = KeyboardType.Number,
            visual = VisualTransformation.None
        )
        item(
            label = "密码",
            message = myModel.password,
            valuelistener = { myModel.onchangepassword(it) },
            hint = "请输入您的密码",
            modifier = Modifier.padding(bottom = 10.dp),
            type = KeyboardType.Password,
            visual = PasswordVisualTransformation()
        )
        Column(Modifier.height(10.dp)) {}
        TextButton(
            onClick = { myModel.onlogin() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(rgb(97, 77, 179)),
                contentColor = Color.White
            )
        ) {
            Text(text = "登录", fontSize = 20.sp)
        }
        Column(Modifier.height(10.dp)) {}
        TextButton(onClick = { homeModel.showregister = true }) {
            Text(text = "注册", color = Color(rgb(31, 117, 209)), fontSize = 20.sp)
        }
    }
    if (myModel.showerror) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            show_errormessage(message = myModel.errormessage) {
                myModel.showerror = false
            }
        }
    }
}

@Composable
fun User() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { myModel.login_detail() }
    ) {
        val (image, name, detail) = createRefs()
        Surface(Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(60.dp))
            .constrainAs(image) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 16.dp)
            }) {
            if (myModel.headphoto){
                AndroidView(factory = { myModel.imageView })
            }
            else Image(painter = painterResource(id = R.drawable.tomato),
                contentDescription = null,)
        }


        Column(
            Modifier
                .fillMaxHeight()
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(image.end, margin = 10.dp)
                }, verticalArrangement = Arrangement.Center
        ) {
            if (homeModel.islogin) {
                Text(text = homeModel.user.name, fontSize = 20.sp)
                Text(text = homeModel.user.id.toString(), fontSize = 20.sp)
            } else {
                Text(text = "未登录", fontSize = 20.sp)
            }
        }

        Icon(painter = painterResource(id = R.drawable.ic_right), contentDescription = null,
            Modifier.constrainAs(detail) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, margin = 16.dp)
            })

    }

}
