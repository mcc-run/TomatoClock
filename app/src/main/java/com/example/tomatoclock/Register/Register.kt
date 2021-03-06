package com.example.tomatoclock.Register

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color.rgb
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tomatoclock.R

//lateinit var register: register_model

var register = register_model()

@Composable
fun RegisterPage(registerModel: register_model) {
    register = registerModel
    ConstraintLayout() {
        val (main, media) = createRefs()
        Column(
            Modifier
                .fillMaxSize()
                .constrainAs(main) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(modifier = Modifier.clickable {
                registerModel.open = true
            }) {
                if (registerModel.ischoose == false)
                    Image(
                        painter = painterResource(id = R.drawable.tomato),
                        contentDescription = null,
                        Modifier
                            .size(60.dp)
                            .clip(
                                RoundedCornerShape(60.dp)
                            )
                    )
                else {
                    AndroidView(factory = { registerModel.image },
                        Modifier
                            .size(60.dp)
                            .clip(
                                RoundedCornerShape(60.dp)
                            ), update = {
                        })
                }
            }
            Text(
                text = "????????????????????????",
                color = Color(rgb(164, 164, 164)),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            item(
                label = "??????",
                registerModel.name,
                valuelistener = { registerModel.onchangename(it) },
                hint = "???????????????",
                modifier = Modifier.padding(bottom = 10.dp),
                KeyboardType.Text,
                VisualTransformation.None
            )
            item(
                label = "??????",
                registerModel.password,
                valuelistener = { registerModel.onchangepassword(it) },
                hint = "???????????????",
                modifier = Modifier.padding(bottom = 10.dp),
                KeyboardType.Password,
                PasswordVisualTransformation()
            )
            item(
                label = "??????",
                registerModel.confirmpass,
                valuelistener = { registerModel.onchangeconfirmpass(it) },
                hint = "?????????????????????",
                modifier = Modifier.padding(bottom = 10.dp),
                KeyboardType.Password,
                PasswordVisualTransformation()
            )
            item(
                label = "??????",
                registerModel.email,
                valuelistener = { registerModel.onchangeemail(it) },
                hint = "?????????????????????",
                modifier = Modifier.padding(bottom = 10.dp),
                KeyboardType.Email,
                VisualTransformation.None
            )
            item(
                label = "??????",
                registerModel.problem,
                valuelistener = { registerModel.onchangeproblem(it) },
                hint = "???????????????",
                modifier = Modifier.padding(bottom = 10.dp),
                KeyboardType.Text,
                VisualTransformation.None
            )
            Text(
                text = "?????????????????????????????????????????????????????????",
                color = Color(rgb(164, 164, 164)),
                modifier = Modifier.padding(bottom = 10.dp)
            )
            item(
                label = "??????",
                registerModel.answer,
                valuelistener = { registerModel.onchangeanswer(it) },
                hint = "???????????????",
                modifier = Modifier.padding(bottom = 10.dp),
                KeyboardType.Text,
                VisualTransformation.None
            )
            TextButton(onClick = { registerModel.onregister() }) {
                Text(text = "??????", fontSize = 30.sp)
            }
        }
//        ???????????????????????????
        if (registerModel.open) {
            Dialog(onDismissRequest = { }) {
                openmedia()
            }
        }
//        ????????????
        if (register.opencrema){
            opencrema(imageuri = register.imageuri)
        }
//        ????????????
        if (register.openalbum){
            openalbum(imageuri = register.imageuri)
        }
//        ??????????????????
        if (registerModel.error) {
            Dialog(onDismissRequest = { }) {
                show_errormessage(register.errormessage,{register.error = false})
            }
        }
    }


}

@Composable
fun show_errormessage(message: String,onclick: () -> Unit) {
    Column(
        Modifier
            .height(100.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, fontSize = 15.sp, modifier = Modifier.padding(10.dp))
        TextButton(onClick = { onclick() }) {
            Text(text = "??????")
        }
    }
}


//???????????????????????????
@Composable
fun openmedia() {
    Column(
        Modifier
            .size(300.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        button_dialog(message = "????????????") { register.opencrema = true }
        Column(Modifier.height(10.dp)) {}
        button_dialog(message = "??????????????????") { register.openalbum = true }
        Column(Modifier.height(10.dp)) {}
        button_dialog(message = "??????") { register.open = false}
    }
}

//???????????????????????????
@Composable
fun button_dialog(message: String, onclick: () -> Unit) {
    TextButton(
        modifier = Modifier
            .size(200.dp, 60.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(1.dp, color = Color(rgb(39, 167, 230)), shape = RoundedCornerShape(50.dp)),
        onClick = { onclick() },
        elevation = ButtonDefaults.elevation(10.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = Color(rgb(38, 167, 230))
        ),

        ) {
        Text(text = message)
    }
}

//????????????
@Composable
fun opencrema(imageuri: Uri) {
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.CAMERA),
            2
        );
    } else {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
        (context as Activity).startActivityForResult(intent, 1)
    }


}

//????????????
@Composable
fun openalbum(imageuri: Uri) {
    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (context as Activity),
            arrayOf(Manifest.permission.CAMERA),
            2
        );
    } else {
        // ?????????????????????
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // ?????????????????????
        intent.type = "image/*"
        (context as Activity).startActivityForResult(intent, 2)
    }
}

//???????????????
@Composable
fun item(
    label: String,
    message: String,
    valuelistener: (String) -> Unit,
    hint: String,
    modifier: Modifier,
    type: KeyboardType,
    visual: VisualTransformation
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${label}:", fontSize = 20.sp)
        TextField(
            maxLines = 1,
            value = message,
            onValueChange = { valuelistener(it) },
            placeholder = { Text(text = hint) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = (type)),
            visualTransformation = visual
        )
    }
}