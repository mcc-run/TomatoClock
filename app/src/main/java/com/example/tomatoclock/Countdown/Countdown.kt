package com.example.tomatoclock.Countdown


import android.graphics.Color.rgb
import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.tomatoclock.R

var countdownModel = Countdown_model()

@Composable
fun CountdownPage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.countdown_bg),
            contentDescription = null,
            Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(Modifier.fillMaxSize()) {
//            倒计时框
            ConstraintLayout(
                Modifier
                    .weight(8f)
                    .fillMaxWidth()
            ) {
                val (Progerss, time) = createRefs()
                CircularProgressIndicator(
                    progress = countdownModel.progress,
                    modifier = Modifier
                        .size(200.dp)
                        .constrainAs(Progerss) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }, color = Color.White, strokeWidth = 10.dp
                )
                Text(
                    text = countdownModel.timeString,
                    fontSize = 30.sp,
                    color = Color.White,
                    modifier = Modifier.constrainAs(time) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }
//            开始暂停按钮
            Row(
                Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { countdownModel.pause_restart() }) {
                    Icon(
                        painter = painterResource(id = if (countdownModel.ispause) R.drawable.ic_action else R.drawable.ic_pause),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Column(Modifier.width(20.dp)) {

                }
                IconButton(onClick = { countdownModel.stop() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_stop),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
            if (countdownModel.showfinish) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    finishPage()
                }
            }
            if (countdownModel.showcustom) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    customPage()
                }
            }
        }
    }
}

@Composable
fun customPage() {
    Column(
        Modifier
            .size(350.dp, 140.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = countdownModel.cancelreason, onValueChange = { countdownModel.cancelreason = it}, colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            textColor = Color(rgb(40, 167, 230))
        ))
        TextButton(onClick = { countdownModel.commit_reason() }) {
            Text(text = "确认")
        }
        TextButton(onClick = { countdownModel.cancel_reason() }) {
            Text(text = "取消")
        }
    }
}

@Composable
fun finishPage() {
    Column(
        Modifier
            .size(400.dp, 300.dp)
            .background(Color.White, RoundedCornerShape(20.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.height(15.dp)) {}
        LazyColumn(Modifier.size(300.dp, 180.dp)) {
            items(countdownModel.finishtype) {
                finishitem(it.reason, { countdownModel.break_countdown(it.id) })
                Column(Modifier.height(5.dp)) {}
            }
        }
        Column(Modifier.height(20.dp)) {}
        finishitem("自定义", {})
        Column(Modifier.height(5.dp)) {}
        finishitem("取消", { countdownModel.break_cancel() })

    }
}

@Composable
fun finishitem(message: String, onclick: () -> Unit) {
    Surface(
        Modifier
            .size(300.dp, 40.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color = Color(rgb(40, 167, 230)), shape = RoundedCornerShape(20.dp))
            .clickable { onclick() },
        elevation = 5.dp
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = message, fontSize = 20.sp, color = Color(rgb(40, 167, 230)))
        }

    }
}