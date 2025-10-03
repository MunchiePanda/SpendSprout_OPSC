package com.example.spendsprout_opsc.reports

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * CircularProgressView - Custom Circular Progress Indicator
 * 
 * This is like Unity's custom UI component for circular progress display.
 * Similar to Unity's custom MonoBehaviour for rendering circular progress bars.
 * 
 * Responsibilities:
 * - Draw circular progress indicator (like Unity's custom UI rendering)
 * - Handle progress animation (like Unity's UI animations)
 * - Display progress values (like Unity's UI Text updates)
 */
class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#333333")
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        color = Color.parseColor("#77B950")
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private var progress = 0.8f // 80% progress
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (minOf(width, height) - 40f) / 2f
        
        rect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        
        // Draw progress arc
        val startAngle = -90f // Start from top
        val sweepAngle = 360f * progress
        canvas.drawArc(rect, startAngle, sweepAngle, false, progressPaint)
    }

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }
}
