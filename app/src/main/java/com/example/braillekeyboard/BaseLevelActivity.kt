package com.example.braillekeyboard

import android.os.Bundle
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

abstract class BaseLevelActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var vibrator: Vibrator
    private lateinit var outputTextView: TextView
    abstract val levelInstructions: List<String>
    private var currentInstructionIndex = 0
    private val pressedKeys = mutableSetOf<Int>() // Menyimpan kombinasi tombol

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        textToSpeech = TextToSpeech(this, this)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        outputTextView = findViewById(R.id.outputTextView)

        setupButtons()
        playInstruction(currentInstructionIndex)
    }

    private fun setupButtons() {
        val buttonIds = listOf(
            R.id.braille_key_1,
            R.id.braille_key_2,
            R.id.braille_key_3,
            R.id.braille_key_4,
            R.id.braille_key_5,
            R.id.braille_key_6
        )

        buttonIds.forEachIndexed { index, id ->
            val button: FrameLayout = findViewById(id)
            button.setOnClickListener {
                onButtonPressed(index + 1)
            }
        }
    }

    private fun onButtonPressed(buttonNumber: Int) {
        vibrator.vibrate(100)

        // Simulasikan validasi pola berdasarkan tombol yang ditekan
        pressedKeys.add(buttonNumber)
        val expectedKeys = levelInstructions[currentInstructionIndex].split(",").map { it.trim().toInt() }.toSet()

        if (pressedKeys == expectedKeys) {
            textToSpeech.speak("Benar.", TextToSpeech.QUEUE_FLUSH, null, null)
            currentInstructionIndex++
            pressedKeys.clear()
            if (currentInstructionIndex < levelInstructions.size) {
                playInstruction(currentInstructionIndex)
            } else {
                textToSpeech.speak("Level selesai. Selamat!", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } else {
            textToSpeech.speak(
                "Salah. Ulangi lagi.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }
    }

    private fun playInstruction(index: Int) {
        val instruction = "Tekan tombol: ${levelInstructions[index]}"
        textToSpeech.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, null)
        outputTextView.text = instruction
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("id", "ID")
            textToSpeech.language = locale
        } else {
            Log.e("TTS", "Inisialisasi TextToSpeech gagal!")
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}
