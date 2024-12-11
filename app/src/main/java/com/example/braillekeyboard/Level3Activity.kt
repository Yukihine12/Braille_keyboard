package com.example.braillekeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.preference.PreferenceManager
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import android.speech.tts.UtteranceProgressListener
import android.view.MotionEvent
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class Level3Activity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var outputTextView: TextView
    private lateinit var instructionTextView: TextView
    private lateinit var vibrator: Vibrator
    private lateinit var editText: EditText
    private val handler = Handler(Looper.getMainLooper())

    private var currentInstructionIndex = 0
    private val brailleMapping = mapOf(
        setOf(2, 4, 5) to 'j',       // j
        setOf(1, 3) to 'k',          // k
        setOf(1, 2, 3) to 'l',       // l
        setOf(1, 3, 4) to 'm',       // m
        setOf(1, 3, 4, 5) to 'n',    // n
        setOf(1, 3, 5) to 'o',       // o
    )

    private var score = 0
    private val MAX_SCORE = 100
    private val instructionsLevel3 = listOf(
        "Masukkan kombinasi untuk huruf J",
        "Masukkan kombinasi untuk huruf K",
        "Masukkan kombinasi untuk huruf L",
        "Masukkan kombinasi untuk huruf M",
        "Masukkan kombinasi untuk huruf N",
        "Masukkan kombinasi untuk huruf O"
    )
    private var outputString: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_level)
        saveLastLevel(3)
        resetScore()

        score = loadScoreFromPreferences() // Muat skor dari penyimpanan

        // Setup TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        // Setup Vibrator
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Setup Output TextView
        outputTextView = findViewById(R.id.outputTextView)

        // Setup Instruction TextView
        instructionTextView = findViewById(R.id.instructionTextView)

        // Setup Score TextView
        val scoreTextView: TextView = findViewById(R.id.scoreTextView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupButtons()
        updateScore(score)
        // Mulai intro setelah semuanya siap
        startIntroduction()
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
            val frameLayout: FrameLayout = findViewById(id)
            val circleTextView: TextView = findViewById(
                when (id) {
                    R.id.braille_key_1 -> R.id.imageTopRight
                    R.id.braille_key_2 -> R.id.imageCenterRight
                    R.id.braille_key_3 -> R.id.imageBottomRight
                    R.id.braille_key_4 -> R.id.imageTopLeft
                    R.id.braille_key_5 -> R.id.imageCenterLeft
                    R.id.braille_key_6 -> R.id.imageBottomLeft
                    else -> throw IllegalArgumentException("Invalid button ID")
                }
            )
            setTouchListener(frameLayout, circleTextView, index + 1)
        }

        buttonIds.forEachIndexed { index, id ->
            val button: FrameLayout = findViewById(id)
            button.setOnClickListener {
                onButtonPressed(index + 1)
            }
        }
    }

    private fun setTouchListener(
        brailleKey: FrameLayout,
        circleTextView: TextView,
        keyId: Int
    ) {
        brailleKey.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    vibrate()
                    userInputCombination.add(keyId)
                    circleTextView.setBackgroundResource(R.drawable.circle_pressed)
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({ onButtonPressed(keyId) }, 1000)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    userInputCombination.remove(keyId)
                    circleTextView.setBackgroundResource(R.drawable.circle_default)
                }
            }
            true
        }
    }

    private fun vibrate() {
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Untuk API 26+
                vibrator.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                // Untuk API lama
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }
        }
    }

    private fun startIntroduction() {
        val introMessage = "Selamat datang di Level 3, Kamu harus mencoba memasukkan kombinasi untuk huruf yang tepat, semoga berhasil."
        textToSpeech.speak(introMessage, TextToSpeech.QUEUE_FLUSH, null, "INTRO")
    }

    private fun navigateToLevel3() {
        Log.d("Level3Activity", "Memulai Level 3")
        currentInstructionIndex = 0
        playLevel3Instruction()
    }

    private val userInputCombination = mutableSetOf<Int>()

    private fun onButtonPressed(buttonNumber: Int) {
        vibrate()// Vibrate untuk memberikan umpan balik
        userInputCombination.add(buttonNumber)
        val correctCombination = brailleMapping.keys.elementAt(currentInstructionIndex)
        if (userInputCombination == correctCombination) {
            // Kombinasi benar
            score += 17 // Tambahkan poin untuk jawaban benar
            if (score > MAX_SCORE) {
                score = MAX_SCORE // Batasi skor maksimum menjadi 100
            }
            updateScore(score) // Perbarui tampilan skor
            saveScoreToPreferences() // Simpan skor setelah memperbarui
            textToSpeech.speak(
                "Benar! Kombinasi untuk huruf ${brailleMapping[userInputCombination]?.uppercase()}.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "CORRECT_LEVEL3"
            )
            userInputCombination.clear() // Reset kombinasi untuk instruksi berikutnya
        } else if (userInputCombination.size == correctCombination.size) {
            // Kombinasi salah
            textToSpeech.speak(
                "Wah, sayang sekali, kombinasi salah nih.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "INCORRECT_LEVEL3"
            )
            userInputCombination.clear() // Reset kombinasi untuk mencoba lagi
        }
    }



    private fun playLevel3Instruction() {
        if (currentInstructionIndex < instructionsLevel3.size) {
            val instruction = instructionsLevel3[currentInstructionIndex]
            textToSpeech.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, "INSTRUCTION_LEVEL3")
            instructionTextView.text = "${instruction.substringAfter("Masukkan ")}"
        } else {
            textToSpeech.speak("Selamat! Anda telah menyelesaikan Level 3. Skor nya adalah $score, waahh semangat yaaa! selanjutnya adalah level 4 nih, semoga berhasil"
                , TextToSpeech.QUEUE_FLUSH, null, "LEVEL_COMPLETED")
        }
    }

    private fun updateScore(score: Int) {
        val scoreTextView: TextView = findViewById(R.id.scoreTextView)
        scoreTextView.text = "Skor: $score"
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale("id", "ID")
            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    runOnUiThread {
                        when (utteranceId) {
                            "INTRO" -> playLevel3Instruction()  // Setelah intro selesai, mulai instruksi
                            "CORRECT_LEVEL3", "INCORRECT_LEVEL3" -> {
                                currentInstructionIndex++
                                playLevel3Instruction()  // Mainkan instruksi berikutnya setelah benar
                            }
                            "LEVEL_COMPLETED" -> {
                                unlockNextLevel( 3) // Membuka Level 3
                                navigateToLevel4()  // Arahkan ke level berikutnya
                            }
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                    Log.e("TTS", "Terjadi kesalahan pada utterance: $utteranceId")
                }
            })
            startIntroduction()  // Mulai intro setelah TextToSpeech siap
        }
    }


    private fun navigateToNextActivity() {
        // Memastikan Anda mengarah ke aktivitas level berikutnya, misalnya Level2Activity
        val intent = Intent(this, Level4Activity::class.java) // Arahkan ke Level 2
        startActivity(intent)
        finish() // Menutup aktivitas saat ini agar tidak bisa kembali
    }


    private fun unlockNextLevel(currentLevel: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nextLevelKey = "level_${currentLevel + 1}" // Level berikutnya
        sharedPreferences.edit()
            .putBoolean(nextLevelKey, true) // Tandai level berikutnya sebagai terbuka
            .apply()
        Log.d("Level3Activity", "Level ${currentLevel + 1} unlocked")
    }

    private fun navigateToLevel4() {
        val intent = Intent(this, Level4Activity::class.java) // Membuat intent untuk berpindah ke Level1Activity
        startActivity(intent) // Memulai aktivitas Level1Activity
        finish() // Menutup aktivitas saat ini agar tidak dapat kembali
    }

    private fun loadScoreFromPreferences(): Int {
        val sharedPreferences = getSharedPreferences("BrailleKeyboardPrefs", Context.MODE_PRIVATE)
        val score = sharedPreferences.getInt("current_score", 0)
        Log.d("Level3Activity", "Score loaded: $score")
        return score
    }


    private fun saveScoreToPreferences() {
        val sharedPreferences = getSharedPreferences("BrailleKeyboardPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("current_score", score) // Simpan skor saat ini
            .apply()
        Log.d("Level3Activity", "Score saved: $score")
    }

    private fun saveLastLevel(level: Int) {
        val sharedPreferences = getSharedPreferences("BrailleKeyboardPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putInt("last_level", level)
            .apply()
        Log.d("Level3Activity", "Last level saved: $level")
    }


    private fun resetScore() {
        score = 0
        saveScoreToPreferences()
        updateScore(score)
    }

    private fun resetLevel() {
        score = 0
        saveScoreToPreferences()
        updateScore(score)
        currentInstructionIndex = 0
        userInputCombination.clear()
        navigateToLevel3()
    }

    override fun onDestroy() {
        resetScore()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}
