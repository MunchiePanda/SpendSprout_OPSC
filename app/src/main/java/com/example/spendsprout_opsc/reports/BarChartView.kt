package com.example.spendsprout_opsc.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.spendsprout_opsc.overview.model.ChartDataPoint

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var data: List<ChartDataPoint> = emptyList()
    private val barPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 30f
    }

    fun setData(data: List<ChartDataPoint>) {
        this.data = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val padding = 50f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding
        val barWidth = chartWidth / data.size

        val maxValue = data.maxOfOrNull { it.revenue }?.toFloat() ?: 0f

        data.forEachIndexed { index, dataPoint ->
            val left = padding + index * barWidth
            val top = padding + chartHeight - (dataPoint.revenue.toFloat() / maxValue * chartHeight)
            val right = left + barWidth - 10
            val bottom = height - padding

            canvas.drawRect(left, top.toFloat(), right, bottom, barPaint)
            canvas.drawText(dataPoint.month, left + (barWidth - 10) / 2, height - 10, textPaint)
        }
    }
}
