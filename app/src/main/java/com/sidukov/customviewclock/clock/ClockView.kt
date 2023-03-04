package com.sidukov.customviewclock.clock

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
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

    lateinit var handSecond: Paint
    lateinit var handMinute: Paint
    lateinit var handHour: Paint

    init {
        val view = View.inflate(context, R.layout.clock_layout, this)
        imageClock = view.findViewById(R.id.image_clock)

        handSecond = paintHand(attrs, 1)
        handMinute = paintHand(attrs, 2)
        handHour = paintHand(attrs, 3)

        angleSecond = (LocalDateTime.now().second.toFloat() * 6f)
        angleMinute = (LocalDateTime.now().minute.toFloat() * 6f)
        angleHour = hour * 30f

        setWillNotDraw(false)

        animateHand(angleSecond, DURATION_SECOND,  1)
        animateHand(angleMinute, DURATION_MINUTE,  2)
        animateHand(angleHour, DURATION_HOUR,  3)

    }

    private fun calculateHour(hour: Float): Float {
        return hour + (LocalDateTime.now().minute.toFloat() * 0.015f)
    }

    @SuppressLint("ResourceAsColor")
    private fun paintHand(attrs: AttributeSet?, handCode: Int): Paint{
        val hand = Paint().apply {
            isAntiAlias = true
            strokeWidth = getHandWidth(attrs, handCode)
            style = Paint.Style.STROKE
        }
        context.withStyledAttributes(attrs, R.styleable.ClockView)  {
            hand.apply {
                color = getHandColor(attrs, handCode)
            }
        }
        return hand
    }

    @SuppressLint("Recycle")
    private fun getHandWidth(attrs: AttributeSet?, code: Int): Float {
        val view = context.obtainStyledAttributes(attrs, R.styleable.ClockView)
        return when(code){
            1 -> view.getFloat(R.styleable.ClockView_second_hand_width, 7f)
            2 -> view.getFloat(R.styleable.ClockView_minute_hand_width, 8f)
            3 -> view.getFloat(R.styleable.ClockView_hour_hand_width, 9f)
            else -> 7f
        }
    }

    @SuppressLint("Recycle")
    private fun getHandColor(attrs: AttributeSet?, code: Int): Int{
        val view = context.obtainStyledAttributes(attrs, R.styleable.ClockView)
        return when(code){
            1 -> view.getColor(R.styleable.ClockView_color_second, BLACK)
            2 -> view.getColor(R.styleable.ClockView_color_minute, BLACK)
            3 -> view.getColor(R.styleable.ClockView_color_hour, BLACK)
            else -> BLACK
        }
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
                handSecond
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
                handMinute
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
                handHour
            )
        }
    }

}
