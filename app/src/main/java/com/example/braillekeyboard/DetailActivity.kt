package com.example.braillekeyboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.RelativeLayout
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import android.speech.tts.TextToSpeech
import android.widget.EditText
//<<<<<<< Updated upstream
//import java.util.Locale
//
//
//class DetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {
//=======
import androidx.core.widget.addTextChangedListener
import java.util.Locale


open class DetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {
//>>>>>>> Stashed changes
    @SuppressLint("ClickableViewAccessibility")

    private val pressedKeys = mutableSetOf<Int>()  // To track the pressed keys
    private var outputString = ""
    private lateinit var editText: EditText
    private lateinit var outputTextView: TextView
    // Mapping from Braille patterns (in terms of key presses) to letters
    private val brailleMapping = mapOf(
        // Angka 0-9
        setOf(3, 4, 5, 6) to '#', // Tanda angka
        setOf(1) to '1',          // 1
        setOf(1, 2) to '2',       // 2
        setOf(1, 4) to '3',       // 3
        setOf(1, 4, 5) to '4',    // 4
        setOf(1, 5) to '5',       // 5
        setOf(1, 2, 4) to '6',    // 6
        setOf(1, 2, 4, 5) to '7', // 7
        setOf(1, 2, 5) to '8',    // 8
        setOf(2, 4) to '9',       // 9
        setOf(2, 4, 5) to '0',    // 0

        // Huruf a-j
        setOf(1) to 'a',          // a
        setOf(1, 2) to 'b',       // b
        setOf(1, 4) to 'c',       // c
        setOf(1, 4, 5) to 'd',    // d
        setOf(1, 5) to 'e',       // e
        setOf(1, 2, 4) to 'f',    // f
        setOf(1, 2, 4, 5) to 'g', // g
        setOf(1, 2, 5) to 'h',    // h
        setOf(2, 4) to 'i',       // i
        setOf(2, 4, 5) to 'j',    // j

        // Huruf k-t
        setOf(1, 3) to 'k',          // k
        setOf(1, 2, 3) to 'l',       // l
        setOf(1, 3, 4) to 'm',       // m
        setOf(1, 3, 4, 5) to 'n',    // n
        setOf(1, 3, 5) to 'o',       // o
        setOf(1, 2, 3, 4) to 'p',    // p
        setOf(1, 2, 3, 4, 5) to 'q', // q
        setOf(1, 2, 3, 5) to 'r',    // r
        setOf(2, 3, 4) to 's',       // s
        setOf(2, 3, 4, 5) to 't',    // t

        // Huruf u-z
        setOf(1, 3, 6) to 'u',          // u
        setOf(1, 2, 3, 6) to 'v',       // v
        setOf(2, 4, 5, 6) to 'w',       // w (pengecualian)
        setOf(1, 3, 4, 6) to 'x',       // x
        setOf(1, 3, 4, 5, 6) to 'y',    // y
        setOf(1, 3, 5, 6) to 'z'
    )

    private lateinit var textToSpeech: TextToSpeech


    private lateinit var gestureDetector: GestureDetector

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        val detailLayout: RelativeLayout = findViewById(R.id.detail)
        gestureDetector = GestureDetector(this, GestureListener())

        detailLayout.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)  // Menangani event gerakan
            true // Mencegah event diteruskan ke elemen lain
        }

        // Ambil data dari Intent yang dikirim dari ItemAdapter
        val title = intent.getStringExtra("title") ?: "Bagas"  // Nama default jika null
                // Nomor default jika tidak ada


        // Set view berdasarkan ID dari activity_detail.xml
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        outputTextView = findViewById(R.id.outputTextView)

        // Atur teks yang diambil dari intent ke dalam view
        nameTextView.text = title

        // Set up padding untuk edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val brailleKey1: FrameLayout = findViewById(R.id.braille_key_1)
        val circleTextView1: TextView = findViewById(R.id.imageTopRight)
        val brailleKey2: FrameLayout = findViewById(R.id.braille_key_2)
        val circleTextView2: TextView = findViewById(R.id.imageCenterRight)
        val brailleKey3: FrameLayout = findViewById(R.id.braille_key_3)
        val circleTextView3: TextView = findViewById(R.id.imageBottomRight)
        val brailleKey4: FrameLayout = findViewById(R.id.braille_key_4)
        val circleTextView4: TextView = findViewById(R.id.imageTopLeft)
        val brailleKey5: FrameLayout = findViewById(R.id.braille_key_5)
        val circleTextView5: TextView = findViewById(R.id.imageCenterLeft)
        val brailleKey6: FrameLayout = findViewById(R.id.braille_key_6)
        val circleTextView6: TextView = findViewById(R.id.imageBottomLeft)


        editText = findViewById(R.id.editText)

        setTouchListener(brailleKey1, circleTextView1, 1, editText)
        setTouchListener(brailleKey2, circleTextView2, 2, editText)
        setTouchListener(brailleKey3, circleTextView3, 3, editText)
        setTouchListener(brailleKey4, circleTextView4, 4, editText)
        setTouchListener(brailleKey5, circleTextView5, 5, editText)
        setTouchListener(brailleKey6, circleTextView6, 6, editText)


        // Example of checking the pressed keys and getting the letter
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Setel bahasa ke bahasa Indonesia
                val locale = Locale("id", "ID")
                val languageStatus = textToSpeech.setLanguage(locale)

                if (languageStatus == TextToSpeech.LANG_MISSING_DATA || languageStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Bahasa Indonesia tidak didukung atau data tidak lengkap.")
                } else {
                    Log.d("TTS", "Bahasa Indonesia berhasil disetel.")
                }
            } else {
                Log.e("TTS", "Text-to-Speech initialization failed!")
            }
        }
//<<<<<<< Updated upstream
//=======

        outputTextView.addTextChangedListener { text ->
            Log.d("BrailleInput", "User typed: $text")
            // Pastikan callback di Level1Activity menerima input ini
        }
//>>>>>>> Stashed changes


    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language for TTS
            val result = textToSpeech.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    override fun onDestroy() {
        // Shutdown TTS to release resources
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

    private var isNumberMode = false

    private val handler = Handler(Looper.getMainLooper())
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        brailleKey: FrameLayout,
        circleTextView: TextView,
        keyId: Int,
        editText: EditText
    ) {
        brailleKey.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Vibrate on key press
                    val vibrator = brailleKey.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (vibrator.hasVibrator()) { // Check if the device supports vibration
                        val vibrationEffect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                        vibrator.vibrate(vibrationEffect)
                    }

                    pressedKeys.add(keyId)  // Add keyId to the set when it's pressed
                    circleTextView.setBackgroundResource(R.drawable.circle_pressed)  // Change background on press
                    Log.d("Touch", "Key $keyId pressed")

                    // Cancel any pending actions (to avoid confusion when pressing new key quickly)
                    handler.removeCallbacksAndMessages(null)

                    // Set a delay of 1 second to check if user is combining keys
                    handler.postDelayed({
                        // Check if the keys are still pressed and update output
                        val typedLetter = getLetterFromPressedKeys()
                        if (typedLetter == '#') {
                            isNumberMode = true // Aktifkan mode angka
                            Log.d("Mode", "Switched to number mode")
                            textToSpeech.speak("Mode angka", TextToSpeech.QUEUE_FLUSH, null, null)
                        } else {
                            val outputChar = if (isNumberMode) {
                                // Map huruf a-j menjadi angka 1-9, 0
                                mapLetterToNumber(typedLetter)
                            } else {
                                typedLetter // Gunakan huruf seperti biasa
                            }

                            outputChar?.let {
                                outputString = it.toString()  // Replace with the new letter/number
                                outputTextView.text = outputString

                                editText.append(it.toString())

                                // Speak the letter or number
                                textToSpeech.speak(it.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
                            }

                            // Jika bukan angka, matikan mode angka
                            if (isNumberMode && outputChar == null) {
                                isNumberMode = false
                                Log.d("Mode", "Switched to letter mode")

                                // Tambahkan suara saat mode huruf diaktifkan
                                textToSpeech.speak("Mode huruf", TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        }

                        Log.d("Output", "Current Output after delay: $outputString")

                    }, 1000) // 1000 milliseconds = 1 second
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    pressedKeys.remove(keyId)  // Remove keyId from the set when it's released
                    circleTextView.setBackgroundResource(R.drawable.circle_default)  // Revert background on release
                    Log.d("Touch", "Key $keyId released")

                    // Clear the outputTextView when the touch is released
                    outputTextView.text = ""  // This will clear the TextView

                    // Log to show that output is cleared
                    Log.d("Output", "Output cleared after release")
                }
            }

            Log.d("Output", "Current Output: $outputString")
            true  // Consume the touch event so it's not passed to other views
        }
    }

    private fun mapLetterToNumber(letter: Char?): Char? {
        return when (letter) {
            'a' -> '1'
            'b' -> '2'
            'c' -> '3'
            'd' -> '4'
            'e' -> '5'
            'f' -> '6'
            'g' -> '7'
            'h' -> '8'
            'i' -> '9'
            'j' -> '0'
            else -> null // Jika huruf di luar jangkauan
        }
    }

    private fun getLetterFromPressedKeys(): Char? {
        return brailleMapping[pressedKeys]
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null && e2 != null) {
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y

                // Detect horizontal swipe
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        // Swipe to the right
                        onSwipeRight()
                    } else {
                        // Swipe to the left
                        onSwipeLeft()
                    }
                }
                // Detect vertical swipe
                else {
                    if (diffY > 0) {
                        // Swipe down
                        onSwipeDown()
                    } else {
                        // Swipe up
                        onSwipeUp()
                    }
                }
            }
            return true
        }
    }

    // Fungsi ketika swipe ke kanan
    // Fungsi ketika swipe ke kanan
    private fun onSwipeRight() {
        println("Swipe ke kanan: Tambah spasi")
        outputString += " " // Tambahkan spasi pada output

        // Update EditText dengan menambahkan spasi
        editText.append(" ")
        textToSpeech.speak("Spasi", TextToSpeech.QUEUE_FLUSH, null, null)
        println("Swipe ke kanan selesai")
    }

    // Fungsi ketika swipe ke kiri
    private fun onSwipeLeft() {
        println("Swipe ke kiri: Hapus karakter terakhir")
        if (editText.text.isNotEmpty()) {
            val currentText = editText.text.toString()
            val updatedText = currentText.dropLast(1) // Hapus karakter terakhir
            editText.setText(updatedText) // Perbarui teks di EditText
            editText.setSelection(updatedText.length) // Pindahkan kursor ke akhir teks
            textToSpeech.speak("Hapus karakter", TextToSpeech.QUEUE_FLUSH, null, null)
        }else {
            // Jika tidak ada karakter untuk dihapus, berikan umpan balik
            textToSpeech.speak(
                "Tidak ada yang dihapus",
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }
        println("Swipe ke kiri selesai")
    }
    // Fungsi ketika swipe ke bawah
    private fun onSwipeDown() {
        // Logika untuk swipe ke bawah
        println("Swipe ke bawah")
    }

    // Fungsi ketika swipe ke atas
    private fun onSwipeUp() {
        // Logika untuk swipe ke atas
        println("Swipe ke atas")
    }
}