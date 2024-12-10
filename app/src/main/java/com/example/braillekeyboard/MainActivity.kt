package com.example.braillekeyboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var lastClickTime: Long = 0
    private val doubleClickThreshold: Long = 300 // Time in milliseconds for double-click detection
    private lateinit var vibrator: Vibrator

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        // Set up the image views
        val dictionaryImageView: ImageView = findViewById(R.id.dictionary)
        val learningImageView: ImageView = findViewById(R.id.learning)
        val resumeImageView: ImageView = findViewById(R.id.resume)

        // Set OnClickListeners for each ImageView to detect single and double clicks
        dictionaryImageView.setOnClickListener { handleImageClick("Dictionary", DictionaryActivity::class.java) }
        learningImageView.setOnClickListener { handleImageClick("Learning", ItemList::class.java) }
        resumeImageView.setOnClickListener { handleImageClick("Resume", ResumeActivity::class.java) }

        // Apply system bar insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Handle TextToSpeech initialization
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = tts.setLanguage(Locale("id", "ID"))
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Bahasa Indonesia tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Inisialisasi TTS gagal", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    // Handle clicks on ImageViews with double-click detection
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleImageClick(imageName: String, activityClass: Class<*>) {
        val currentTime = System.currentTimeMillis()
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)) // 100ms vibration
        if (currentTime - lastClickTime < doubleClickThreshold) {
            // Double-click detected: Start the associated activity
            val intent = Intent(this, activityClass)
            startActivity(intent)
        } else {
            // Single-click detected: Speak the image name using TTS
            tts.speak(imageName, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        // Update the last click time
        lastClickTime = currentTime
    }

    // Method to handle clicks for "dictionary" image
    fun goToDictionaryPage(view: View) {
        val intent = Intent(this, DictionaryActivity::class.java)
        startActivity(intent)
    }

    // Method to handle clicks for "learning" image
    fun goToItemListPage(view: View) {
        val intent = Intent(this, ItemList::class.java)
        startActivity(intent)
    }

    // Method to handle clicks for "level" image
    fun goToResumePage(view: View) {
        val intent = Intent(this, ResumeActivity::class.java)
        startActivity(intent)
    }
}
