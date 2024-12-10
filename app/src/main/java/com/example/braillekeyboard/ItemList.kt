package com.example.braillekeyboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale

class ItemList : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: List<Item>
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_list)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemList = listOf(
            Item("Level 1", "A-D", R.drawable.play),
            Item("Level 2", "E-H", R.drawable.play),
            Item("Level 3", "I-L", R.drawable.play),
            Item("Level 4", "M-P", R.drawable.play),
            Item("Level 5", "Q-T", R.drawable.play),
            Item("Level 6", "A-Z", R.drawable.play),
        )

        // Pass TextToSpeech instance to the adapter
        adapter = ItemAdapter(itemList, tts)
        recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to Indonesian
            val langResult = tts.setLanguage(Locale("id", "ID"))
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Bahasa Indonesia tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Inisialisasi TTS gagal", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        // Shutdown TTS when the activity is destroyed
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}