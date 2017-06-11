package com.gregmcgowan.fivesorganiser.team

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.DITHER_FLAG
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.gregmcgowan.fivesorganiser.R


class PitchView : View {

    val paint: Paint = Paint(DITHER_FLAG)

    private val mainGreen: Int
    private val alternateGreen: Int

    private var sectionHeight = 0f

    private var mainHeight = 0f
    private var mainWidth = 0f
    private var numberOfSections = 0f;

    private var smallMargin = 0f
    private var lineThickness = 0f

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.mainGreen = ContextCompat.getColor(context, R.color.green_700)
        this.alternateGreen = ContextCompat.getColor(context, R.color.green_600)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.mainHeight = h.toFloat()
        this.mainWidth = w.toFloat()

        sectionHeight = resources.getDimension(R.dimen.pitch_section_height)
        numberOfSections = mainHeight / sectionHeight

        smallMargin = resources.getDimension(R.dimen.activity_horizontal_margin)
        lineThickness = resources.getDimension(R.dimen.pitch_line_thickness)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.let {
            var numberOfSections = 0
            var top = 0f

            paint.style = Paint.Style.FILL
            while (top < mainHeight) {
                val color =  if(numberOfSections % 2 == 0) { mainGreen } else
                    alternateGreen

                paint.color =color
                canvas.drawRect(0f, top, mainWidth, top + sectionHeight, paint)
                top += sectionHeight
                numberOfSections++
            }

            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = lineThickness
            canvas.drawRect(smallMargin, sectionHeight, mainWidth - smallMargin, mainHeight - smallMargin, paint)
        }
    }

}
