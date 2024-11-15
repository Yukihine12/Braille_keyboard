package com.example.braillekeyboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dictionaryImageView: ImageView = findViewById(R.id.dictionary)
        val learningImageView: ImageView = findViewById(R.id.learning)
        val resumeImageView: ImageView = findViewById(R.id.resume)

    }

    // Metode untuk menangani klik pada gambar "dictionary"
    fun goToDictionaryPage(view: View) {
        val intent = Intent(this, DictionaryActivity::class.java)
        startActivity(intent)
    }

    // Metode untuk menangani klik pada gambar "learning"
    fun goToItemListPage(view: View) {
        val intent = Intent(this, ItemList::class.java)
        startActivity(intent)
    }

    // Metode untuk menangani klik pada gambar "level"
    fun goToResumePage(view: View) {
        val intent = Intent(this, ResumeActivity::class.java)
        startActivity(intent)
    }
}
