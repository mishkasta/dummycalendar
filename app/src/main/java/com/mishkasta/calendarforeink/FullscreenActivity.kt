package com.mishkasta.calendarforeink

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mishkasta.calendarforeink.databinding.ActivityFullscreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

const val REPEAT_PERIOD_MILLIS = 10 * 60 * 1000L

class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        val calendar = Calendar.getInstance()
        updateView(calendar)

        val delayMills = getDelayMilliseconds(calendar)
        startCoroutineTimer(delayMills) {
            updateView()
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    private inline fun startCoroutineTimer(
        delayMillis: Long = 0,
        crossinline action: () -> Unit) = GlobalScope.launch(Dispatchers.Main) {
        delay(delayMillis)
        while (true) {
            action()
            delay(REPEAT_PERIOD_MILLIS)
        }
    }

    private fun getDelayMilliseconds(calendar: Calendar) : Long {
        val minutes = calendar.get(Calendar.MINUTE)
        val minutesBeforeTen = 10 - (minutes % 10)

        return minutesBeforeTen * 60 * 1000L
    }

    private fun updateView() {
        val calendar = Calendar.getInstance()
        updateView(calendar)
    }

    private fun updateView(calendar: Calendar) {
        val date = calendar.time

        val locale = Locale("RU")
        with(binding) {
            dayNameText.text = SimpleDateFormat("EEEE", locale).format(date.time)
            monthText.text = SimpleDateFormat("MMMM", locale).format(date.time)
            yearText.text = SimpleDateFormat("yyyy", locale).format(date.time)
            dayText.text = SimpleDateFormat("d", locale).format(date.time)
            batteryLevelText.text = getBatteryPercentage().toString()
        }
    }

    private fun getBatteryPercentage(): Int? {
        val batteryStatus: Intent? = registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        return batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale
        }
    }
}