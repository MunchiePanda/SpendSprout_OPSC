package com.example.spendsprout_opsc.overview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.spendsprout_opsc.overview.model.ChartDataPoint

/**
 * ChartView - Custom UI Component for Drawing Charts
 * 
 * This is like Unity's custom UI component or a custom MonoBehaviour that handles rendering.
 * Similar to Unity's OnGUI() method or custom LineRenderer component.
 * 
 * Responsibilities:
 * - Draw income vs expenses line chart (like Unity's Graphics.DrawLine())
 * - Handle custom rendering with Canvas (like Unity's GL drawing)
 * - Manage chart data and visualization (like Unity's data visualization scripts)
 */
class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var chartData: List<ChartDataPoint> = emptyList()
    private val incomePaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 6f
        isAntiAlias = true
    }
    private val expensePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 6f
        isAntiAlias = true
    }
    private val axisPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 2f
    }

    fun setData(data: List<ChartDataPoint>) {
        chartData = data
        invalidate()
    }

    /**
     * onDraw() - Like Unity's OnGUI() or custom rendering method
     * This is called every frame to render the chart
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (chartData.isEmpty()) return

        // Get view dimensions - like Unity's Screen.width/height or RectTransform
        val width = this.width.toFloat()
        val height = this.height.toFloat()
        val padding = 50f
        val chartWidth = width - 2 * padding
        val chartHeight = height - 2 * padding

        // Draw axes - like Unity's Graphics.DrawLine() or LineRenderer
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint)
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint)

        // Draw data lines - revenue and target
        drawLine(canvas, chartData.map { it.revenue.toFloat() }, incomePaint, padding, chartWidth, chartHeight)
        drawLine(canvas, chartData.map { it.target.toFloat() }, expensePaint, padding, chartWidth, chartHeight)
    }

    private fun drawLine(canvas: Canvas, values: List<Float>, paint: Paint, padding: Float, chartWidth: Float, chartHeight: Float) {
        if (values.isEmpty()) return

        val maxValue = values.maxOrNull() ?: 1.0f
        val stepX = chartWidth / (values.size - 1)

        for (i in 0 until values.size - 1) {
            val x1 = padding + i * stepX
            val y1 = padding + chartHeight - (values[i] / maxValue * chartHeight)
            val x2 = padding + (i + 1) * stepX
            val y2 = padding + chartHeight - (values[i + 1] / maxValue * chartHeight)
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
    }
}

