package com.example.braillekeyboard

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DictionaryDetail : AppCompatActivity() {

    private lateinit var tts: TextToSpeech
    private lateinit var brailleMap: Map<Set<Int>, Char>
    private var currentItemIndex: Int = 0
    private lateinit var itemList: List<ItemDictionary>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_detail)

        val detailImageView: ImageView = findViewById(R.id.detailImageView)
        val backButton: TextView = findViewById(R.id.backButton)
        val nextButton: TextView = findViewById(R.id.nextButton)
        val backTextView: TextView = findViewById(R.id.back)

        // Ambil data yang diteruskan
        currentItemIndex = intent.getIntExtra("ITEM_INDEX", 0)
        itemList = intent.getParcelableArrayListExtra("ITEM_LIST") ?: emptyList()

        // Tampilkan gambar
        detailImageView.setImageResource(itemList[currentItemIndex].imageResId)

        // Inisialisasi TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("id", "ID") // Atur ke bahasa Indonesia
            }
        }

        // Inisialisasi kombinasi Braille
        brailleMap = mapOf(
            // Angka
            setOf(1) to '1', setOf(1, 2) to '2', setOf(1, 4) to '3',
            setOf(1, 4, 5) to '4', setOf(1, 5) to '5', setOf(1, 2, 4) to '6', setOf(1, 2, 4, 5) to '7',
            setOf(1, 2, 5) to '8', setOf(2, 4) to '9', setOf(2, 4, 5) to '0',

            // Huruf a-j
            setOf(1) to 'a', setOf(1, 2) to 'b', setOf(1, 4) to 'c', setOf(1, 4, 5) to 'd',
            setOf(1, 5) to 'e', setOf(1, 2, 4) to 'f', setOf(1, 2, 4, 5) to 'g', setOf(1, 2, 5) to 'h',
            setOf(2, 4) to 'i', setOf(2, 4, 5) to 'j',

            // Huruf k-t
            setOf(1, 3) to 'k', setOf(1, 2, 3) to 'l', setOf(1, 3, 4) to 'm', setOf(1, 3, 4, 5) to 'n',
            setOf(1, 3, 5) to 'o', setOf(1, 2, 3, 4) to 'p', setOf(1, 2, 3, 4, 5) to 'q', setOf(1, 2, 3, 5) to 'r',
            setOf(2, 3, 4) to 's', setOf(2, 3, 4, 5) to 't',

            // Huruf u-z
            setOf(1, 3, 6) to 'u', setOf(1, 2, 3, 6) to 'v', setOf(2, 4, 5, 6) to 'w',
            setOf(1, 3, 4, 6) to 'x', setOf(1, 3, 4, 5, 6) to 'y', setOf(1, 3, 5, 6) to 'z'
        )

        // Klik pada gambar memicu TTS
        detailImageView.setOnClickListener {
            speakBrailleDescription(itemList[currentItemIndex].title)
        }

        // Tombol navigasi
        nextButton.setOnClickListener {
            if (currentItemIndex < itemList.size - 1) {
                currentItemIndex++
                updateDetail()
            }
        }

        backButton.setOnClickListener {
            if (currentItemIndex > 0) {
                currentItemIndex--
                updateDetail()
            }
        }

        backTextView.setOnClickListener { finish() }

        // Bacakan huruf saat ini saat huruf dipilih
        speakBrailleDescription(itemList[currentItemIndex].title)
    }

    private fun updateDetail() {
        val currentItem = itemList[currentItemIndex]
        findViewById<ImageView>(R.id.detailImageView).setImageResource(currentItem.imageResId)
        speakBrailleDescription(currentItem.title)
    }

    private fun speakBrailleDescription(title: String) {
        val brailleChar = title.last().lowercaseChar()  // Mengambil karakter terakhir
        val combination: Set<Int>? = when {
            brailleChar.isDigit() -> {
                // Jika karakter adalah angka, cari kombinasi untuk angka
                val digit = brailleChar.toString().toInt() // Mengubah karakter menjadi angka
                // Mengembalikan kombinasi angka dari peta berdasarkan angka
                getCombinationForDigit(digit)
            }
            brailleChar.isLetter() -> {
                // Jika huruf, cari kombinasi untuk huruf
                brailleMap.entries.find { it.value == brailleChar }?.key
            }
            else -> null
        }

        // Jika kombinasi ditemukan, tampilkan deskripsi
        val description = combination?.joinToString(", ") { "Tombol $it" } ?: "Kombinasi tidak ditemukan"

        // Tentukan output berdasarkan tipe karakter (angka atau huruf)
        val output = when {
            brailleChar.isDigit() -> {
                // Jika angka, sebutkan angka dengan kombinasi Braille
                "Angka $brailleChar menggunakan kombinasi $description"
            }
            brailleChar.isLetter() -> {
                // Jika huruf, sebutkan huruf dengan kombinasi Braille
                "Huruf $brailleChar menggunakan kombinasi $description"
            }
            else -> {
                // Jika karakter tidak dikenali
                "Karakter tidak dikenali"
            }
        }

        // Bacakan output melalui TTS
        tts.speak(output, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // Fungsi untuk mendapatkan kombinasi angka
    private fun getCombinationForDigit(digit: Int): Set<Int>? {
        return when (digit) {
            1 -> setOf(1)
            2 -> setOf(1, 2)
            3 -> setOf(1, 4)
            4 -> setOf(1, 4, 5)
            5 -> setOf(1, 5)
            6 -> setOf(1, 2, 4)
            7 -> setOf(1, 2, 4, 5)
            8 -> setOf(1, 2, 5)
            9 -> setOf(2, 4)
            0 -> setOf(2, 4, 5)
            else -> null
        }
    }


    override fun onDestroy() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }
}
