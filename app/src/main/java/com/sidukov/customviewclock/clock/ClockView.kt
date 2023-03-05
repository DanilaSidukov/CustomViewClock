package com.sidukov.customviewclock.clock

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.RED
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withRotation
import com.sidukov.customviewclock.R
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("InflateParams")
class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private var angleSecond = 0f
    private var angleMinute = 0f
    private var angleHour = 0f
    private var hour =
        if (LocalDateTime.now().hour.toFloat() < 13) calculateHour(LocalDateTime.now().hour.toFloat())
        else calculateHour(LocalDateTime.now().hour.toFloat() - 12)

    private val DURATION_SECOND = 60000L
    private val DURATION_MINUTE = 3600000L
    private val DURATION_HOUR = 86400000L

    private var imageClock: ImageView

    private lateinit var handSecondPaint: Paint
    private lateinit var handMinutePaint: Paint
    private lateinit var handHourPaint: Paint

    init {
        val view = View.inflate(context, R.layout.clock_layout, this)
        imageClock = view.findViewById(R.id.image_clock)

        context.withStyledAttributes(attrs, R.styleable.ClockView) {

            handHourPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = getColor(R.styleable.ClockView_hourHandColor, RED)
                strokeWidth = getFloat(
                    R.styleable.ClockView_hourHandWidth, 9f
                )
            }

            handMinutePaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = getColor(R.styleable.ClockView_minuteHandColor, BLACK)
                strokeWidth = getFloat(
                    R.styleable.ClockView_minuteHandWidth, 8f
                )
            }

            handSecondPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = getColor(R.styleable.ClockView_secondHandColor, BLACK)
                strokeWidth = getFloat(
                    R.styleable.ClockView_secondHandWidth, 7f
                )
            }
        }

        angleSecond = (LocalDateTime.now().second.toFloat() * 6f)
        angleMinute = (LocalDateTime.now().minute.toFloat() * 6f)
        angleHour = hour * 30f

        setWillNotDraw(false)

        animateHand(angleSecond, DURATION_SECOND, 1)
        animateHand(angleMinute, DURATION_MINUTE, 2)
        animateHand(angleHour, DURATION_HOUR, 3)

    }

    private fun calculateHour(hour: Float): Float {
        return hour + (LocalDateTime.now().minute.toFloat() * 0.015f)
    }

    fun setHourHandColor(color: Int) {
        handHourPaint.color = color
        invalidate()
    }

    fun setMinuteHandColor(color: Int) {
        handMinutePaint.color = color
        invalidate()
    }

    fun setSecondHandColor(color: Int) {
        handSecondPaint.color = color
        invalidate()
    }

    private fun animateHand(
        startRange: Float,
        durationMilliseconds: Long,
        handCode: Int,
    ) {
        when (handCode) {
            1 -> {
                ValueAnimator.ofFloat(startRange, 360f).apply {
                    duration = durationMilliseconds
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        angleSecond = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }.doOnEnd {
                    ValueAnimator.ofFloat(0f, 360f).apply {
                        duration = durationMilliseconds
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            angleSecond = it.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                }
            }
            2 -> {
                ValueAnimator.ofFloat(startRange, 360f).apply {
                    duration = durationMilliseconds
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        angleMinute = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }.doOnEnd {
                    ValueAnimator.ofFloat(0f, 360f).apply {
                        duration = durationMilliseconds
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            angleMinute = it.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                }
            }
            3 -> {
                ValueAnimator.ofFloat(startRange, 360f).apply {
                    duration = durationMilliseconds
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        angleHour = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }.doOnEnd {
                    ValueAnimator.ofFloat(0f, 360f).apply {
                        duration = durationMilliseconds
                        repeatCount = ValueAnimator.INFINITE
                        interpolator = LinearInterpolator()
                        addUpdateListener {
                            angleHour = it.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
                }
            }
        }

    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        canvas.withRotation(angleSecond,
            (imageClock.right / 2).toFloat(),
            (imageClock.bottom / 2).toFloat()
        ) {
            drawLine(
                imageClock.measuredWidth / 2f,
                imageClock.measuredHeight / 1.87f,
                imageClock.measuredWidth / 2f,
                imageClock.top.toFloat() + (imageClock.height / 5.5f),
                handSecondPaint
            )
        }
        canvas.withRotation(angleMinute,
            (imageClock.right / 2).toFloat(),
            (imageClock.bottom / 2).toFloat()
        ) {
            drawLine(
                imageClock.measuredWidth / 2f,
                imageClock.measuredHeight / 1.9f,
                imageClock.measuredWidth / 2f,
                imageClock.top.toFloat() + (imageClock.height / 6),
                handMinutePaint
            )
        }
        canvas.withRotation(angleHour,
            (imageClock.right / 2).toFloat(),
            (imageClock.bottom / 2).toFloat()
        ) {
            drawLine(
                imageClock.measuredWidth / 2f,
                imageClock.measuredHeight / 1.9f,
                imageClock.measuredWidth / 2f,
                imageClock.top.toFloat() + (imageClock.height / 4),
                handHourPaint
            )
        }
    }

}
