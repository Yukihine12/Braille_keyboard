package com.example.braillekeyboard

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Ambil data dari Intent yang dikirim dari ItemAdapter
        val title = intent.getStringExtra("title") ?: "Bagas"  // Nama default jika null
        val number = intent.getIntExtra("number", 6)            // Nomor default jika tidak ada


        // Set view berdasarkan ID dari activity_detail.xml
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val numberTextView: TextView = findViewById(R.id.numberTextView)

        // Atur teks yang diambil dari intent ke dalam view
        nameTextView.text = title
        numberTextView.text = number.toString()
      6

        // Set up padding untuk edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Atur gambar buletan jika diperlukan, contoh untuk buletan kiri atas
        val buletTopLeft: ImageView = findViewById(R.id.buletTopLeft)
        buletTopLeft.setImageResource(R.drawable.bulet)

        // Atur gambar buletan lainnya (misalnya buletCenterLeft, buletBottomLeft, dll.)
        val buletCenterLeft: ImageView = findViewById(R.id.buletCenterLeft)
        buletCenterLeft.setImageResource(R.drawable.bulet)

        val buletBottomLeft: ImageView = findViewById(R.id.buletBottomLeft)
        buletBottomLeft.setImageResource(R.drawable.bulet)

        val buletTopRight: ImageView = findViewById(R.id.buletTopRight)
        buletTopRight.setImageResource(R.drawable.bulet)

        val buletCenterRight: ImageView = findViewById(R.id.buletCenterRight)
        buletCenterRight.setImageResource(R.drawable.bulet)

        val buletBottomRight: ImageView = findViewById(R.id.buletBottomRight)
        buletBottomRight.setImageResource(R.drawable.bulet)
    }
}
