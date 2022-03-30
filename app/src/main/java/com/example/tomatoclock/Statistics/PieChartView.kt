package com.example.mfchart

import com.example.tomatoclock.R
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.ui.unit.dp
import androidx.core.view.size
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import okhttp3.internal.notifyAll
import java.lang.StringBuilder


class PieChartView(context: Context?) : LinearLayout(context) {
    private var mPieChart: PieChart? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.pie_layout)
//        initView()
//    }
    init {
        LayoutInflater.from(context).inflate(R.layout.pie_layout,this)
        initView()
    }

    //初始化
    private fun initView() {

        //折现饼状图
        mPieChart = findViewById(R.id.mPieChart) as PieChart?
        mPieChart!!.setUsePercentValues(false)   //以百分比形式显示数据
        mPieChart!!.description.isEnabled = false
        mPieChart!!.setExtraOffsets(0f, 0f, 0f, 0f) //这是饼状图显示位置
        mPieChart!!.isDrawHoleEnabled = false
        // 触摸旋转
        mPieChart!!.isRotationEnabled = true
        mPieChart!!.isHighlightPerTapEnabled = true


        mPieChart!!.setEntryLabelColor(Color.rgb(154, 59, 209))


        //默认动画
        mPieChart?.animateY(1000, Easing.EasingOption.EaseInOutQuad)
        val l = mPieChart!!.legend
            l.isEnabled = false
    }

    //设置数据
    public fun setData(entries: ArrayList<PieEntry>) {
        val dataSet = PieDataSet(entries, "专注时长分布")
        dataSet.sliceSpace = 3f     //设置各个版块之间的缝隙
        dataSet.selectionShift = 5f     //设置饼状图大小

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())



        dataSet.colors = colors

        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        data.setValueFormatter(object : IValueFormatter{
            override fun getFormattedValue(
                value: Float,
                entry: Entry?,
                dataSetIndex: Int,
                viewPortHandler: ViewPortHandler?
            ): String {
                val v = value.toInt()
                var s = StringBuilder()
                val hour = v / 60
                if (hour != 0)s.append("${hour}小时")
                val minute = v % 60
                if (minute != 0)s.append("${minute}分钟")
                return s.toString()
            }
        })

        mPieChart!!.data = data

        mPieChart!!.highlightValues(null)
        mPieChart!!.invalidate()

    }


}


