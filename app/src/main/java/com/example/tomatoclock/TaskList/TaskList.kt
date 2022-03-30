package com.example.tomatoclock.TaskList

import android.graphics.Color.rgb
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.SetSelectionCommand
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.tomatoclock.Countdown.countdownModel
import com.example.tomatoclock.R
import java.util.*

lateinit var tasklistModel : tasklist_model

@Composable
fun TaskListPage(tasklist : tasklist_model) {
    tasklistModel= tasklist
    TaskListView(tasklistModel = tasklistModel)

}

@Composable
fun TaskListView(tasklistModel: tasklist_model) {
//    任务列表界面
    Box(Modifier) {
        Column() {
//        标题栏
            tasklist_title(
                Modifier
                    .fillMaxSize()
                    .weight(0.8f)
                    .background(Color(rgb(175, 156, 222))), tasklistModel = tasklistModel
            )
//        分隔栏
            Column(Modifier.weight(0.2f)) {}
//        主界面
            mainview(
                Modifier
                    .fillMaxWidth()
                    .weight(9f), tasklistModel = tasklistModel
            )
            if (tasklistModel.addtask) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    dialogpage(
                        Modifier,
                        tasklistModel = tasklistModel,
                        { tasklistModel.oncancell_add()},
                        { tasklistModel.oncommit_add() }
                    )
                }
            }

            if (tasklistModel.showcalendar) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    calendarPage()
                }
            }
            if (tasklistModel.showCustom) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    show_degital(tasklistModel = tasklistModel)
                }
            }
            if (tasklistModel.showerror) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    erroemesssage(tasklistModel.error, { tasklistModel.showerror = false })
                }
            }

        }

    }
    

}

@Composable
fun dialogpage(
    modifier: Modifier,
    tasklistModel: tasklist_model,
    oncancel: () -> Unit,
    oncommit: () -> Unit
) {
    Column(
        modifier = modifier
            .width(400.dp)
            .height(330.dp)
            .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        标题栏
        dialog_title(
            Modifier
                .weight(1f), "添加任务", { oncancel() }, { oncommit() }
        )
//        任务名
        dialog_taskname(
            Modifier
                .weight(1f), tasklistModel
        )
        Column(Modifier.height(5.dp)) {

        }
//        计时模式
        dialog_time_model(
            Modifier
                .weight(1f), tasklistModel
        )


        if (tasklistModel.model_selected == 2) {
//            目标卡
            dialog_target(
                Modifier
                    .weight(3f)
            )
        }

        if (tasklistModel.model_selected == 3) {
//            习惯卡
            dialog_habit(
                Modifier
                    .weight(3f)
            )
        }

        //        普通番茄钟选项卡
        dialog_normal(
            Modifier
                .weight(1f)
        )
        Column(Modifier.height(5.dp)) {

        }
        if (tasklistModel.time_selected == 1) {
            //        计时细节
            dialog_normal_detail(
                Modifier
                    .weight(1f)
            )
        }


    }
}

//习惯选项卡
@Composable
fun dialog_habit(modifier: Modifier) {
    Column(modifier = modifier.width(300.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//        第一行
        Row(
            Modifier
                .weight(1f)
                .width(200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "我想", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)
//            下拉框
            ConstraintLayout() {

                Row(
                    Modifier
                        .width(60.dp)
                        .padding(start = 5.dp)
                        .clickable { tasklistModel.open_select = !tasklistModel.open_select },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = tasklistModel.habit)
                    Icon(
                        painter = painterResource(id = R.drawable.ic_nabla),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = tasklistModel.open_select,
                    onDismissRequest = { },
//                content = { Icon(painter = painterResource(id = R.drawable.ic_yes), contentDescription = null)}
                ) {

                    DropdownMenuItem(onClick = { tasklistModel.onchange_habit_selected(1) }) {
                        Text("每天")
                    }
                    DropdownMenuItem(onClick = { tasklistModel.onchange_habit_selected(2) }) {
                        Text("每周")
                    }
                    DropdownMenuItem(onClick = { tasklistModel.onchange_habit_selected(3) }) {
                        Text("每月")
                    }
                }
            }
        }
        Row(
            Modifier
                .weight(1f)
                .width(200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "完成", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)

            BasicTextField(
                value = "${tasklistModel.count_habit}",
                onValueChange = { tasklistModel.onchange_count_habit(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    color = Color(rgb(167, 154, 201)), fontSize = 16.sp,
                    baselineShift = BaselineShift.None
                ),
                modifier = Modifier.width(50.dp)
            )
            Text(text = "小时", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)
        }
        Row(
            Modifier
                .weight(1f)
                .width(300.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "最后一步，设置单次专注的时长", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)

        }
    }
}

//目标选项卡
@Composable
fun dialog_target(modifier: Modifier) {
    Column(modifier = modifier.width(300.dp)) {
        Row(
            Modifier
                .weight(1f)
                .width(300.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "我想在", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)
            Row(Modifier.size(120.dp, 30.dp)) {
                dialog_button(
                    modifier = modifier,
                    { tasklistModel.opencalendar() },
                    { true },
                    { tasklistModel.date })
            }
            Text(text = "之前", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)
        }
        Row(
            Modifier
                .weight(1f)
                .width(300.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "一共完成", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)
            BasicTextField(
                value = "${tasklistModel.count_time}",
                onValueChange = { tasklistModel.onchange_count_time(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    color = Color(rgb(167, 154, 201)), fontSize = 16.sp,
                    baselineShift = BaselineShift.None
                ),
            )
            Text(text = "小时", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)
        }
        Row(
            Modifier
                .weight(1f)
                .width(300.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "最后一步，设置单次专注的时长", color = Color(rgb(145, 145, 145)), fontSize = 16.sp)

        }
    }
}

//日历
@Composable
fun calendarPage() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        AndroidView(
            factory = {
                CalendarView(it)
            },
            modifier = Modifier.fillMaxWidth(),
            update = {

                it.minDate = tasklistModel.mindate.time
                it.setOnDateChangeListener { view, year, month, day ->
                    tasklistModel.setyear(year)
                    tasklistModel.setmonth(month + 1)
                    tasklistModel.setday(day)
                }

            }
        )
        Button(onClick = { tasklistModel.closecalendar() }) {
            Text(text = "确定")
        }
    }

}

//自定义按钮，添加自定义时间
@Composable
fun show_degital(tasklistModel: tasklist_model) {
    Column(
        Modifier.background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//      输入框
        TextField(
            value = "${tasklistModel.Customtime}",
            onValueChange = { tasklistModel.onchange_Customtime(it) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
//        确认按钮
        Button(onClick = { tasklistModel.oncommit_Customtime() }) {
            Text(text = "确定")
        }
    }
}

//普通番茄选项卡,选择时长
@Composable
fun dialog_normal_detail(modifier: Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (index in 0..2) {
            dialog_button(
                Modifier.padding(end = 10.dp),
                { tasklistModel.onchange_times_selected(index + 1) },
                { tasklistModel.gettimes_selected(index + 1) },
                { tasklistModel.gettimesname(index) })
        }
    }
}


//普通番茄选项卡
@Composable
fun dialog_normal(modifier: Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (index in 0..2) {
            dialog_button(
                Modifier.padding(end = 10.dp),
                { tasklistModel.onchange_time_selected(index + 1) },
                { tasklistModel.gettime_selected(index + 1) },
                { tasklistModel.gettimename(index) })
        }
    }
}

//计时模式
@Composable
fun dialog_time_model(modifier: Modifier, tasklistModel: tasklist_model) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (index in 0..2) {
            dialog_button(Modifier.padding(end = 10.dp),
                { tasklistModel.onchange_model_selected(index + 1) },
                { tasklistModel.getmodel_selected(index + 1) },
                { tasklistModel.getmodelname(index) })
        }
    }
}

@Composable
fun dialog_button(
    modifier: Modifier,
    onclick: () -> Unit,
    oncolor: () -> Boolean,
    ontext: () -> String
) {
    Button(
        onClick = { onclick() }, colors = ButtonDefaults.buttonColors(
            backgroundColor = if (oncolor()) Color(rgb(236, 245, 252))
            else Color(rgb(244, 244, 244)),
            contentColor = if (oncolor()) Color(rgb(81, 178, 221))
            else Color(rgb(145, 145, 145))
        ),
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 0.dp)
    ) {
        Text(text = ontext())
    }
}

//任务名
@Composable
fun dialog_taskname(modifier: Modifier, tasklistModel: tasklist_model) {
    Column() {
        TextField(
            value = tasklistModel.taskname,
            onValueChange = { tasklistModel.taskname = it },
            placeholder = { Text(text = "请输入任务名") },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color(rgb(167, 154, 201)),
                placeholderColor = Color(rgb(167, 154, 201))
            )
        )

    }
}

//添加任务     错误提示信息   没填写任务名或时间
@Composable
fun erroemesssage(text: String, onclick: () -> Unit) {
    Column(
        Modifier
            .background(Color.White)
            .size(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(text = text)
        Button(onClick = { onclick() }) {
            Text(text = "确定")
        }
    }
}

//标题栏
@Composable
fun dialog_title(
    modifier: Modifier,
    titlename: String,
    oncancel: () -> Unit,
    oncommit: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .width(400.dp)
            .background(
                color = Color(rgb(227, 227, 227)),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        val (title, yes, no) = createRefs()
//        标题
        Text(
            text = titlename, Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 16.dp)
            },
            fontSize = 30.sp,
            color = Color(rgb(94, 94, 94))
        )
//        取消按钮
        IconButton(onClick = { oncancel() }, Modifier.constrainAs(no) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, margin = 5.dp)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_no), contentDescription = null)
        }
//        添加按钮
        IconButton(onClick = { oncommit() }, Modifier.constrainAs(yes) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(no.start, margin = 5.dp)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_yes), contentDescription = null)
        }
    }
}

//主界面
@Composable
fun mainview(modifier: Modifier, tasklistModel: tasklist_model) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn() {
            items(tasklistModel.tasks) {
                taskitem(it.taskname, if (it.time_count == 0) "" else "${it.time_count}分钟",
                    { countdownModel.start(it) })
                Column(Modifier.height(5.dp)) {
                }
            }
        }

    }
}

//任务项
@Composable
fun taskitem(name: String, time: String,onstart : ()->Unit) {
    ConstraintLayout(
        modifier = Modifier
            .width(370.dp)
            .height(70.dp)
            .background(Color(rgb(203, 222, 156)), shape = RoundedCornerShape(10.dp))
    ) {
        val (taskname, detail, taskstart) = createRefs()
//        任务名
        Text(
            text = name, Modifier.constrainAs(taskname) {
                top.linkTo(parent.top, margin = 5.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }, fontSize = 23.sp, color = Color(rgb(208, 156, 222))
        )
//        事件信息
        Text(
            text = time, Modifier.constrainAs(detail) {
                bottom.linkTo(parent.bottom, margin = 5.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }, fontSize = 18.sp, color = Color(rgb(208, 156, 222))
        )
//        开始按钮
        TextButton(onClick = { onstart() }, Modifier.constrainAs(taskstart) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, margin = 16.dp)
        }) {
            Text(text = "开始", fontSize = 23.sp, color = Color(rgb(208, 156, 222)))
        }
    }

}


//标题栏
@Composable
fun tasklist_title(modifier: Modifier, tasklistModel: tasklist_model) {
    ConstraintLayout(modifier = modifier) {
        val (title, add) = createRefs()
//        标题
        Text(text = "任务列表", Modifier.constrainAs(title) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, margin = 16.dp)
        }, fontSize = 25.sp, color = Color.White)
//        添加任务按钮
        IconButton(onClick = { tasklistModel.onchange_add() }, Modifier.constrainAs(add) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, margin = 16.dp)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun dialogpreview() {
    val tasklistModel = tasklist_model()
    dialogpage(
        Modifier
            .size(400.dp, 300.dp)
            .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
        tasklistModel = tasklistModel,{tasklistModel.oncancell_add()},{tasklistModel.oncommit_add()}
    )
}

//@Preview(showBackground = true)
//@Composable
//fun taskpreview() {
//    val tasklistModel = tasklist_model()
//    TaskListView(tasklistModel = tasklistModel)
//}

