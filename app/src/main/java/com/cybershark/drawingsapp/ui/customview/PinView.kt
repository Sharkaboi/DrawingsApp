package com.cybershark.drawingsapp.ui.customview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.cybershark.drawingsapp.R
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class PinView @JvmOverloads constructor(context: Context?, attr: AttributeSet? = null) :
    SubsamplingScaleImageView(context, attr) {
    private val paint = Paint()
    private val vPin = PointF()
    private var sPin: PointF? = null
    private var pin: Bitmap? = null

    fun setPin(sPin: PointF?) {
        this.sPin = sPin
        initialise()
        invalidate()
    }

    private fun initialise() {
        val density = resources.displayMetrics.densityDpi.toFloat()
        pin = (ResourcesCompat.getDrawable(this.resources, R.drawable.ic_marker, null) as VectorDrawable).toBitmap()
        if(pin!=null) {
            val w = density / 420f * pin!!.width
            val h = density / 420f * pin!!.height
            pin = Bitmap.createScaledBitmap(pin!!, w.toInt(), h.toInt(), true)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        paint.isAntiAlias = true
        if (sPin != null && pin != null) {
            sourceToViewCoord(sPin, vPin)
            val vX = vPin.x - pin!!.width / 2
            val vY = vPin.y - pin!!.height
            canvas.drawBitmap(pin!!, vX, vY, paint)
        }
    }

    init {
        //initialise()
    }
}