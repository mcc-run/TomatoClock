package com.example.mfchart.linechart


import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.tomatoclock.R
import com.example.tomatoclock.TaskSet.set
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.StringBuilder


class LineChartView(context: Context) : LinearLayout(context)
//    , OnChartGestureListener,
//    OnChartValueSelectedListener
{
    private var mLineChar: LineChart? = null
    private var set1: LineDataSet? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.line_layout,this)
        initView()
    }



    //初始化View
    private fun initView() {

        mLineChar = findViewById(R.id.mLineChar) as LineChart?

        //设置描述文本
        mLineChar!!.description.isEnabled = false
        //设置缩放
        mLineChar!!.isDragEnabled = true
        //设置推动
        //如果禁用,扩展可以在x轴和y轴分别完成
        mLineChar!!.setPinchZoom(true)


        mLineChar!!.axisRight.isEnabled = false
        mLineChar!!.xAxis.position = XAxis.XAxisPosition.BOTTOM
        mLineChar!!.xAxis.setDrawGridLines(false)
        mLineChar!!.axisLeft.setDrawGridLines(false)
        mLineChar?.axisLeft?.valueFormatter = object : IAxisValueFormatter{
            override fun getDecimalDigits(): Int {
                return 0
            }

            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                return "${value.toInt()}分钟"
            }
        }

        //默认动画
        mLineChar!!.animateXY(1000,1000)

        // 得到这个文字
        val l = mLineChar!!.legend
        l.isEnabled = false

    }

    public fun setxAxis(x : IAxisValueFormatter){
        mLineChar?.xAxis?.valueFormatter = x
    }

    //传递数据集
    public fun setData(values: MutableList<Entry>,label : String) {
        if (mLineChar!!.data != null && mLineChar!!.data.dataSetCount > 0) {
            set1 = mLineChar!!.data.getDataSetByIndex(0) as LineDataSet
            set1!!.setValues(values)
            mLineChar!!.data.notifyDataChanged()
            mLineChar!!.notifyDataSetChanged()
        } else {
            // 创建一个数据集,并给它一个类型
            set1 = LineDataSet(values, label)
            set1?.valueFormatter = object : IValueFormatter {
                override fun getFormattedValue(
                    value: Float,
                    entry: Entry?,
                    dataSetIndex: Int,
                    viewPortHandler: ViewPortHandler?
                ): String {
                    val v = value.toInt()
                    val hour = v / 60
                    var s = StringBuilder()
                    if (hour != 0)s.append("${hour}小时")
                    val minute = v % 60
                    if (minute != 0)s.append("${minute}分钟")
                    return s.toString()
                }
            }

            // 在这里设置线
            set1!!.enableDashedLine(10f, 0f, 0f)
            set1!!.color = Color.BLACK  /*设置线的颜色*/
            set1!!.setCircleColor(Color.BLACK)  //设置顶点的颜色
            set1!!.lineWidth = 1f       //设置线的宽度
//            set1!!.circleRadius = 0f    //设置顶点圆的半径
//            set1!!.setDrawCircleHole(false)     //false 为实心圆
            set1!!.setDrawCircles(false)
            set1!!.valueTextSize = 10f      //设置数据的字体大小
            set1!!.setDrawFilled(true)      //区域内填充
            set1!!.formSize = 15f
            set1!!.mode = LineDataSet.Mode.CUBIC_BEZIER
            if (Utils.getSDKInt() >= 18) {
                set1!!.fillColor = Color.rgb(125, 202, 240)
            } else {
                set1!!.fillColor = Color.BLACK
            }
            val dataSets = ArrayList<ILineDataSet>()
            //添加数据集
            dataSets.add(set1!!)

            //创建一个数据集的数据对象
            val data = LineData(dataSets)

            //设置数据
            mLineChar!!.data = data
        }
    }

//    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartGesture) {}
//    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartGesture) {
//        // 完成之后停止晃动
//        if (lastPerformedGesture != ChartGesture.SINGLE_TAP) mLineChar!!.highlightValues(null)
//    }
//
//    override fun onChartLongPressed(me: MotionEvent) {}
//    override fun onChartDoubleTapped(me: MotionEvent) {}
//    override fun onChartSingleTapped(me: MotionEvent) {}
//    override fun onChartFling(
//        me1: MotionEvent,
//        me2: MotionEvent,
//        velocityX: Float,
//        velocityY: Float
//    ) {
//    }
//
//    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {}
//    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {}
//    fun onValueSelected(e: Map.Entry<*, *>?, h: Highlight?) {}
//    override fun onValueSelected(e: Entry?, h: Highlight?) {
//
//    }
//
//    override fun onNothingSelected() {}


}