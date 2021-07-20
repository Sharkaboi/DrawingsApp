package com.cybershark.drawingsapp.ui.custom_views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.cybershark.drawingsapp.R
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class PinView @JvmOverloads constructor(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {
    private val paint = Paint()
    private val viewCoordinatePoint = PointF()
    private var scaledMarkerBitmap: Bitmap? = null
    private lateinit var listOfPoints: List<PointF>


    private fun initialise() {
        val screenDensity = resources.displayMetrics.densityDpi.toFloat()
        scaledMarkerBitmap = (ResourcesCompat.getDrawable(this.resources, R.drawable.ic_marker, null) as VectorDrawable).toBitmap()
        if (scaledMarkerBitmap != null) {
            val w = screenDensity / 420f * scaledMarkerBitmap!!.width
            val h = screenDensity / 420f * scaledMarkerBitmap!!.height
            scaledMarkerBitmap = Bitmap.createScaledBitmap(scaledMarkerBitmap!!, w.toInt(), h.toInt(), true)
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        paint.isAntiAlias = true

        //iterating through points and drawing
        listOfPoints.forEach { sourceCoordinatePoint ->
            if (scaledMarkerBitmap != null) {
                sourceToViewCoord(sourceCoordinatePoint, viewCoordinatePoint)
                val vX = viewCoordinatePoint.x - scaledMarkerBitmap!!.width / 2
                val vY = viewCoordinatePoint.y - scaledMarkerBitmap!!.height
                canvas.drawBitmap(scaledMarkerBitmap!!, vX, vY, paint)
            }
        }

    }

    fun setPins(listOfPoints: List<PointF>) {
        this.listOfPoints = listOfPoints
        initialise()
        invalidate()
    }

}