package com.example.beeptimeroffline

import android.media.*
import android.os.*
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var repeat = true

    private lateinit var tone: ToneGenerator
    private var volume = 70
    private var soundType = 0 // 0 = Beep, 1 = Bell

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        tone = ToneGenerator(AudioManager.STREAM_MUSIC, volume)

        val minutesInput = findViewById<EditText>(R.id.minutesInput)
        val warningInput = findViewById<EditText>(R.id.warningInput)
        val repeatCheck = findViewById<CheckBox>(R.id.repeatCheck)
        val startBtn = findViewById<Button>(R.id.startBtn)
        val stopBtn = findViewById<Button>(R.id.stopBtn)
        val soundSpinner = findViewById<Spinner>(R.id.soundSpinner)
        val volumeSeek = findViewById<SeekBar>(R.id.volumeSeek)

        soundSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Beep", "Bell")
        )

        soundSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                soundType = pos
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        volumeSeek.progress = volume
        volumeSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                volume = progress
                tone.release()
                tone = ToneGenerator(AudioManager.STREAM_MUSIC, volume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        startBtn.setOnClickListener {
            val minutes = minutesInput.text.toString().toLongOrNull() ?: 1
            val warning = warningInput.text.toString().toLongOrNull() ?: 3
            repeat = repeatCheck.isChecked

            startTimer(minutes * 60000, warning * 1000)
        }

        stopBtn.setOnClickListener {
            timer?.cancel()
        }
    }

    private fun playSound(duration: Int) {
        if (soundType == 0) {
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, duration)
        } else {
            tone.startTone(ToneGenerator.TONE_PROP_ACK, duration)
        }
    }

    private fun startTimer(total: Long, warning: Long) {
        timer?.cancel()

        timer = object : CountDownTimer(total, 1000) {
            override fun onTick(ms: Long) {
                if (ms <= warning) {
                    playSound(80)
                }
            }

            override fun onFinish() {
                playSound(150)
                if (repeat) startTimer(total, warning)
            }
        }.start()
    }
}
