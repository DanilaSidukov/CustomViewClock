package com.sidukov.customviewclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sidukov.customviewclock.clock.ClockView

class MainActivity : AppCompatActivity() {

    private lateinit var clockView: ClockView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockView = findViewById(R.id.clock_view)

    }
}