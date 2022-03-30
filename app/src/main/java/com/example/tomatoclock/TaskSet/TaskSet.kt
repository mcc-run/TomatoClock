package com.example.tomatoclock.TaskSet

import android.graphics.Color.rgb
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.tomatoclock.R
import com.example.tomatoclock.TaskList.*

lateinit var tasksetModel: taskset_model

@Composable
fun TaskSetPage(taskset: taskset_model) {
    tasksetModel = taskset
    Column(Modifier) {
        //    标题栏
        taskset_title(
            Modifier
                .fillMaxSize()
                .weight(0.8f)
                .background(Color(rgb(175, 156, 222)))
        )
        //        分隔栏
//        Column(Modifier.weight(0.2f)) {}
        //        主界面
        mainview_taskset(
            Modifier
                .fillMaxWidth()
                .weight(9f)
        )

        if (tasksetModel.addset) {
            Dialog(onDismissRequest = { /*TODO*/ }) {
                AddsetPage()
            }
        }


        if (tasksetModel.addtask) {
            Dialog(onDismissRequest = { /*TODO*/ }) {
                dialogpage(
                    Modifier, tasklistModel = tasklistModel, { tasksetModel.addtask = false },
                    { tasksetModel.gettask() }
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
        if (tasksetModel.showerror) {
            Dialog(onDismissRequest = { /*TODO*/ }) {
                erroemesssage(tasksetModel.error, { tasksetModel.showerror = false })
            }
        }
    }

}

@Composable
fun AddsetPage() {
    Column(
        modifier = Modifier
            .width(400.dp)
            .height(100.dp)
            .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        标题栏
        dialog_title(
            Modifier
                .weight(1f),
            "添加任务集合",
            { tasksetModel.addset = false },
            { tasksetModel.oncommit_sets() }
        )
//        任务名
        Column() {
            TextField(
                value = tasksetModel.setname,
                onValueChange = { tasksetModel.setname = it },
                placeholder = { Text(text = "请输入任务名") },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    textColor = Color(rgb(167, 154, 201)),
                    placeholderColor = Color(rgb(167, 154, 201))
                )
            )

        }
    }
}

@Composable
fun taskset_title(modifier: Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (title, add) = createRefs()
//        标题
        Text(text = "任务集", Modifier.constrainAs(title) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start, margin = 16.dp)
        }, fontSize = 25.sp, color = Color.White)
//        添加任务按钮
        IconButton(onClick = { tasksetModel.addset = true }, Modifier.constrainAs(add) {
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

@Composable
fun mainview_taskset(modifier: Modifier) {
    Column(modifier) {
        LazyColumn() {
            itemsIndexed(tasksetModel.sets) { index, it ->
                setitem(it, index)
            }
        }
    }
}

//任务集子项
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun setitem(set: set, index: Int) {
    Column(verticalArrangement = Arrangement.Center) {
//        任务集子项
        Row(
            Modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
//            右边框
            Row(
                Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(Color(rgb(175, 156, 222)))
            ) {

            }

            Column() {
                ConstraintLayout(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val (setname, detail, statistic, addset, refresh) = createRefs()
//        任务名
                    Text(
                        text = set.name, Modifier.constrainAs(setname) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start, margin = 5.dp)
                            bottom.linkTo(parent.bottom)
                        }, fontSize = 23.sp, color = Color(rgb(110, 110, 110))
                    )
//                    展开按钮
                    IconButton(
                        onClick = { tasksetModel.unfold(index) },
                        Modifier
                            .rotate(tasksetModel.rotates[index])
                            .constrainAs(detail) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(statistic.start, margin = 5.dp)
                            }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_detail),
                            contentDescription = null,
                            tint = Color(rgb(110, 110, 110))
                        )
                    }
//                    统计数据按钮
                    IconButton(onClick = { }, Modifier.constrainAs(statistic) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(addset.start, margin = 5.dp)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_statistic),
                            contentDescription = null,
                            tint = Color(rgb(110, 110, 110))
                        )
                    }
//                    添加任务按钮
                    IconButton(
                        onClick = { tasksetModel.addtasks(index) },
                        Modifier.constrainAs(addset) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end, margin = 16.dp)
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = Color(rgb(110, 110, 110))
                        )
                    }

                }
            }
        }
        AnimatedVisibility(visible = tasksetModel.visibles[index]) {
            unfolditem(set.taskset)
        }
    }
}

//任务集展开项
@Composable
fun unfolditem(taskset: MutableList<Task>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (task in taskset) {
            set_taskitem(
                task.taskname,
                if (task.time_selected == 2) "正向计时" else if (task.time_selected == 3) "不计时" else "${task.time_count}分钟",
                { tasksetModel.commit_tasklist(task) })
            Column(Modifier.height(5.dp)) {}
        }

    }
}

//任务子项
@Composable
fun set_taskitem(name: String, time: String, onclick: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .width(370.dp)
            .height(70.dp)
            .background(
                Color(rgb(175, 156, 222)),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        val (taskname, detail, addtask) = createRefs()
//        任务名
        Text(
            text = name, Modifier.constrainAs(taskname) {
                top.linkTo(parent.top, margin = 5.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }, fontSize = 23.sp, color = Color(rgb(203, 222, 156))
        )
//        事件信息
        Text(
            text = time, Modifier.constrainAs(detail) {
                bottom.linkTo(parent.bottom, margin = 5.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }, fontSize = 18.sp, color = Color(rgb(203, 222, 156))
        )
//        添加按钮
        IconButton(onClick = { onclick() }, Modifier.constrainAs(addtask) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end, margin = 16.dp)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                tint = Color(rgb(203, 222, 156))
            )
        }
    }

}
