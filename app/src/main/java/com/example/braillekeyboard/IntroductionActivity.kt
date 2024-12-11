package com.example.braillekeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class IntroductionActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var outputTextView: TextView
    private lateinit var vibrator: Vibrator

    private var currentInstructionIndex = 0
    private val instructions = listOf(
        "Tombol satu berada di ujung kiri atas.",
        "Tombol dua berada di ujung kiri tengah.",
        "Tombol tiga berada di ujung kiri bawah.",
        "Tombol empat berada di ujung kanan atas.",
        "Tombol lima berada di ujung kanan tengah.",
        "Tombol enam berada di ujung kanan bawah."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Setup TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        // Setup Vibrator
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Setup Output TextView
        outputTextView = findViewById(R.id.outputTextView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButtons()
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
            val button: FrameLayout = findViewById(id)
            button.setOnClickListener {
                onButtonPressed(index + 1)
            }
        }
    }


    private fun startIntroduction() {
        val introMessage = "Selamat datang di halaman pengenalan. Di sini Anda akan menjalankan tutorial mengenai posisi tombol-tombol pada keyboard braille."
        textToSpeech.speak(introMessage, TextToSpeech.QUEUE_FLUSH, null, "INTRO")
    }

    private fun onButtonPressed(buttonNumber: Int) {
        // Vibrate on button press
        vibrator.vibrate(100)

        if (buttonNumber == currentInstructionIndex + 1) {
            // Correct button
            textToSpeech.speak("Tombol $buttonNumber benar.", TextToSpeech.QUEUE_FLUSH, null, "CORRECT")
        } else {
            // Tombol yang salah
            textToSpeech.speak(
                "Tombol salah. Anda menekan tombol $buttonNumber. ${instructions[currentInstructionIndex]}",
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }
    }

    private fun playInstruction(index: Int) {
        val instruction = instructions[index]
        textToSpeech.speak(instruction, TextToSpeech.QUEUE_FLUSH, null, "INSTRUCTION")
        outputTextView.text = "${instruction.substringAfter("Tombol ")}"
        outputTextView.textSize = 14f // Adjust text size
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("id", "ID")
            val result = textToSpeech.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Bahasa Indonesia tidak didukung atau data tidak lengkap.")
            } else {
                // Start introduction
                startIntroduction()
            }

            // Set TTS listener
            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Tidak ada aksi khusus saat TTS mulai berbicara
                }

                override fun onDone(utteranceId: String?) {
                    runOnUiThread {
                        when (utteranceId) {
                            "INTRO" -> playInstruction(currentInstructionIndex) // Mulai tutorial setelah intro
                            "CORRECT" -> {
                                currentInstructionIndex++
                                if (currentInstructionIndex < instructions.size) {
                                    playInstruction(currentInstructionIndex) // Mainkan instruksi berikutnya
                                } else {
                                    // Semua instruksi selesai, beri ucapan selamat sebelum berpindah
                                    val completionMessage = "Selamat, anda telah menyelesaikan introduction tutorial. Selanjutnya adalah level satu. Semoga berhasil dan selamat bermain."
                                    textToSpeech.speak(completionMessage, TextToSpeech.QUEUE_FLUSH, null, "LEVEL1_NAVIGATION")
                                }
                            }
                            "LEVEL1_NAVIGATION" -> {
                                unlockNextLevel(0)
                                // Pindah ke Level 1 setelah ucapan selesai
                                navigateToLevel1()
                            }
                        }
                    }
                }


                override fun onError(utteranceId: String?) {
                    // Error dengan utterance tertentu
                    Log.e("TTS", "Error terjadi pada utterance: $utteranceId")
                }
            })

        } else {
            Log.e("TTS", "Inisialisasi TextToSpeech gagal!")
        }
    }

    private fun unlockNextLevel(currentLevel: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nextLevelKey = "level_${currentLevel + 1}" // Level berikutnya
        sharedPreferences.edit()
            .putBoolean(nextLevelKey, true) // Tandai level berikutnya sebagai terbuka
            .apply()
        Log.d("IntroductionActivity", "Level ${currentLevel + 1} unlocked")
    }

    private fun navigateToLevel1() {
        unlockNextLevel(0) // Hanya panggil unlockNextLevel di sini, tanpa rekursi
        Log.d("IntroductionActivity", "Navigating to Level1Activity") // Logging untuk debugging
        val intent = Intent(this, Level1Activity::class.java) // Membuat intent untuk berpindah ke Level1Activity
        startActivity(intent) // Memulai aktivitas Level1Activity
        finish() // Menutup aktivitas saat ini agar tidak dapat kembali
    }



    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}
