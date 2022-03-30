package com.rengwuxian.wecompose.ui

import android.graphics.Color.rgb
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tomatoclock.R

@Composable
fun WeBottomBar(selected: Int, onSelectedChanged: (Int) -> Unit) {
  Row(Modifier.background(Color.White)) {
    TabItem(
      R.drawable.ic_tasklist, "任务列表",
      if (selected == 0) Color(rgb(159, 141, 215)) else Color(rgb(136, 138, 151)),
      Modifier
        .weight(1f)
        .clickable {
          onSelectedChanged(0)
        }
    )
    TabItem(
      R.drawable.ic_taskset, "任务集",
      if (selected == 1) Color(rgb(159, 141, 215)) else Color(rgb(136, 138, 151)),
      Modifier
        .weight(1f)
        .clickable {
          onSelectedChanged(1)
        }
    )
    TabItem(
      R.drawable.ic_statistic, "统计数据",
      if (selected == 2) Color(rgb(159, 141, 215)) else Color(rgb(136, 138, 151)),
      Modifier
        .weight(1f)
        .clickable {
          onSelectedChanged(2)
        }
    )
    TabItem(
      R.drawable.ic_my, "我的",
      if (selected == 3) Color(rgb(159, 141, 215)) else Color(rgb(136, 138, 151)),
      Modifier
        .weight(1f)
        .clickable {
          onSelectedChanged(3)
        }
    )
  }
}

@Composable
fun TabItem(@DrawableRes iconId: Int, title: String, tint: Color, modifier: Modifier = Modifier) {
  Column(
    modifier.padding(vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(painterResource(iconId), title, Modifier.size(24.dp), tint = tint)
    Text(title, fontSize = 11.sp, color = tint)
  }
}
