package com.example.braillekeyboard

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DictionaryDetail : AppCompatActivity() {

    private var currentItemIndex: Int = 0
    private lateinit var itemList: List<ItemDictionary>  // Daftar item yang diteruskan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_detail)
        enableEdgeToEdge()

        // Ambil ImageView dari layout
        val imageView: ImageView = findViewById(R.id.detailImageView)

        // Ambil resource ID gambar dan itemIndex dari Intent
        val imageResId = intent.getIntExtra("IMAGE_RES_ID", -1)
        currentItemIndex = intent.getIntExtra("ITEM_INDEX", -1)
        itemList = intent.getParcelableArrayListExtra("ITEM_LIST") ?: emptyList()

        // Set gambar ke ImageView jika resource ID valid
        if (imageResId != -1) {
            imageView.setImageResource(imageResId)
        }

        // Tombol Next untuk navigasi ke item berikutnya
        val nextButton: TextView = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            if (currentItemIndex < itemList.size - 1) {
                val nextItem = itemList[currentItemIndex + 1]
                val intent = Intent(this, DictionaryDetail::class.java)
                intent.putExtra("IMAGE_RES_ID", nextItem.imageResId)
                intent.putExtra("ITEM_INDEX", currentItemIndex + 1)
                intent.putParcelableArrayListExtra("ITEM_LIST", ArrayList(itemList))
                startActivity(intent)
            }
        }

        // Tombol Back untuk kembali ke aktivitas sebelumnya
        val backButton: TextView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
        val back: TextView = findViewById(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this, DictionaryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  // Clear all previous activities from the stack
            startActivity(intent)
            finish()  // Menutup aktivitas saat ini
        }
    }
}
