package com.example.mfchart.barchart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.tomatoclock.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler

class BarChartView(context: Context?) : LinearLayout(context) {
    private var mBarChart: BarChart? = null

//    protected override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.bar_layout)
//        initView()
//    }
    init {
        LayoutInflater.from(context).inflate(R.layout.bar_layout,this)
        initView()
    }

    //初始化
    private fun initView() {

        //条形图
        mBarChart = findViewById(R.id.mBarChart) as BarChart?
        mBarChart!!.setDrawValueAboveBar(true)  //数值显示在条的上方
        mBarChart!!.description.isEnabled = false


        //是否显示表格颜色
        mBarChart!!.setDrawGridBackground(false)
        val xAxis: XAxis = mBarChart!!.xAxis
        xAxis.valueFormatter = object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return "${value.toInt()}点"
            }

            override fun getDecimalDigits(): Int {
                return 0
            }
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setDrawGridLines(false)

        xAxis.setGranularity(1f)    //设置条的最大方法极限

        val leftAxis: YAxis = mBarChart!!.axisLeft
        leftAxis.valueFormatter = object : IAxisValueFormatter {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return "${value.toInt()}分钟"
            }
            override fun getDecimalDigits(): Int {
                return 0
            }
        }
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.setSpaceTop(15f)
        leftAxis.setAxisMinimum(0f)
        leftAxis.setDrawGridLines(false)
        mBarChart?.axisRight?.isEnabled = false
        // 设置标示
        val l: Legend = mBarChart!!.legend
        l.isEnabled = false
    }

    //设置数据
    public fun setData(yVals1: ArrayList<BarEntry>) {
        val set1: BarDataSet

        if (mBarChart!!.data != null &&
            mBarChart!!.data.dataSetCount > 0
        ) {
            set1 = mBarChart!!.data.getDataSetByIndex(0) as BarDataSet
            set1.setValues(yVals1)
            mBarChart!!.data.notifyDataChanged()
            mBarChart!!.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(yVals1, "2017年工资涨幅")
            set1.valueFormatter = object : IValueFormatter{
                override fun getFormattedValue(
                    value: Float,
                    entry: Entry?,
                    dataSetIndex: Int,
                    viewPortHandler: ViewPortHandler?
                ): String {
                    return value.toInt().toString()
                }
            }
            //设置有四种颜色
            set1.colors = ColorTemplate.MATERIAL_COLORS.toList()  //设置数据显示的颜色
            val dataSets: ArrayList<IBarDataSet> = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setBarWidth(0.9f)
            //设置数据
            mBarChart!!.data = data
        }
        mBarChart?.invalidate()
    }
}