package com.gregmcgowan.fivesorganiser.core.ui

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.gregmcgowan.fivesorganiser.core.R


class PlayerAvatar : View {

    private val path = Path()
    private val paint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val shadowPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mainCircleBackgroundColour: Int
    private val shadowColor: Int

    private val radius = 2f

    private val mainCircleMargin: Float
    private val shadowPadding: Float
    private val headYOffset: Float
    private val headRadiusOffset: Float
    private val bodyOvalRadius: Float
    private val bodyHorizontalMargin: Float
    private val neckMargin: Float
    private val strokeWidth: Float

    private var mainCircleRadius: Float = 0f
    private var middleX: Float = 0f
    private var middleY: Float = 0f

    private var headX: Float = 0f
    private var headY: Float = 0f
    private var headRadius: Float = 0f

    private var bodyTop: Float = 0f
    private var bodyLeft: Float = 0f
    private var bodyRight: Float = 0f
    private var bodyBottom: Float = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mainCircleBackgroundColour = ContextCompat.getColor(context, R.color.grey_300)
        shadowColor = ContextCompat.getColor(context, R.color.grey_400)
        shadowPaint.color = shadowColor
        shadowPaint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER)
        shadowPaint.style = Paint.Style.STROKE

        val resources = resources
        mainCircleMargin = resources.getDimension(R.dimen.player_avatar_main_circle_margin)
        shadowPadding = resources.getDimension(R.dimen.player_avatar_shadow_padding)

        headYOffset = resources.getDimension(R.dimen.player_avatar_head_y_offset)
        headRadiusOffset = resources.getDimension(R.dimen.player_avatar_head_radius_offset)

        bodyOvalRadius = resources.getDimension(R.dimen.player_avatar_body_oval_radius)
        bodyHorizontalMargin = resources.getDimension(R.dimen.player_avatar_body_margin)

        neckMargin = resources.getDimension(R.dimen.player_avatar_neck_margin)
        strokeWidth = resources.getDimension(R.dimen.player_avatar_body_cutoff_stroke_width)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mainCircleRadius = w.div(2f) - mainCircleMargin
        middleX = w.div(2f)
        middleY = h.div(2f)

        headX = middleX
        headY = middleY - headYOffset
        headRadius = (mainCircleRadius / 3f) + headRadiusOffset

        bodyLeft = bodyHorizontalMargin
        bodyTop = headY + headRadius + neckMargin
        bodyRight = w - bodyHorizontalMargin
        bodyBottom = h + bodyOvalRadius
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        canvas.let {
            // main circle with shadow
            paint.style = Paint.Style.FILL
            paint.color = mainCircleBackgroundColour
            //canvas?.drawCircle(middleX, middleY, mainRadius + shadowPadding, shadowPaint)
            canvas?.drawCircle(middleX, middleY, mainCircleRadius, paint)

            //Head circle
            paint.color = Color.WHITE
            canvas?.drawCircle(headX, headY, headRadius, paint)

            //Clip outside the main circle
            path.addCircle(middleX, middleY, mainCircleRadius, Path.Direction.CW)
            canvas?.clipPath(path)

            //Body oval
            paint.color = Color.WHITE
            canvas?.drawOval(bodyLeft, bodyTop,
                    bodyRight, bodyBottom, paint)

            //add a cut off circle around the body
            paint.color = mainCircleBackgroundColour
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            canvas?.drawCircle(middleX, middleY, mainCircleRadius - (strokeWidth / 2f), paint)
        }


    }

}


