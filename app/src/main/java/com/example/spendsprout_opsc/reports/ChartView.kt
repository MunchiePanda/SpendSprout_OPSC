package com.example.spendsprout_opsc.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.spendsprout_opsc.reports.model.ChartDataPoint

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var chartData: List<ChartDataPoint> = emptyList()
    private val revenuePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 4f
        isAntiAlias = true
    }
    private val targetPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 4f
        isAntiAlias = true
    }
    private val axisPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 2f
    }
    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 1f
        alpha = 50
    }

    fun setData(data: List<ChartDataPoint>) {
        chartData = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (chartData.isEmpty()) return

        val width = this.width.toFloat()
        val height = this.height.toFloat()
        val padding = 60f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding

        // Draw grid lines
        drawGrid(canvas, padding, chartWidth, chartHeight)

        // Draw axes
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint)
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint)

        // Draw lines
        drawLine(canvas, chartData.map { it.revenue.toFloat() }, revenuePaint, padding, chartWidth, chartHeight)
        drawLine(canvas, chartData.map { it.target.toFloat() }, targetPaint, padding, chartWidth, chartHeight)
    }

    private fun drawGrid(canvas: Canvas, padding: Float, chartWidth: Float, chartHeight: Float) {
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (i * chartHeight / gridLines)
            canvas.drawLine(padding, y, padding + chartWidth, y, gridPaint)
        }
    }

    private fun drawLine(canvas: Canvas, values: List<Float>, paint: Paint, padding: Float, chartWidth: Float, chartHeight: Float) {
        if (values.isEmpty()) return

        val maxValue = values.maxOrNull() ?: 1.0f
        val stepX = chartWidth / (values.size - 1).toFloat()

        for (i in 0 until values.size - 1) {
            val x1 = padding + i * stepX
            val y1 = padding + chartHeight - (values[i] / maxValue * chartHeight)
            val x2 = padding + (i + 1) * stepX
            val y2 = padding + chartHeight - (values[i + 1] / maxValue * chartHeight)
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
    }
}

