package com.example.braillekeyboard

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DictionaryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DictionaryAdapter
    private lateinit var itemList: List<ItemDictionary>  // Daftar itemDictionary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)
        enableEdgeToEdge()

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Buat daftar huruf dan angka
        itemList = mutableListOf<ItemDictionary>().apply {
            // Tambahkan huruf A-Z
            for (ch in 'A'..'Z') {
                add(ItemDictionary("Huruf $ch", getDrawableIdForLetter(ch)))
            }
            // Tambahkan angka 0-9
            for (i in 0..9) {
                add(ItemDictionary("Angka $i", getDrawableIdForNumber(i)))
            }
        }

        // Pasang adapter ke RecyclerView
        adapter = DictionaryAdapter(itemList) { item, position ->
            navigateToDetailActivity(item, position)
        }
        recyclerView.adapter = adapter

        val back: TextView = findViewById(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP  // Clear all previous activities from the stack
            startActivity(intent)
            finish()  // Menutup aktivitas saat ini
        }
    }

    private fun getDrawableIdForNumber(number: Int): Int {
        return when (number) {
            0 -> R.drawable.number_0
            1 -> R.drawable.number_1
            2 -> R.drawable.number_2
            3 -> R.drawable.number_3
            4 -> R.drawable.number_4
            5 -> R.drawable.number_5
            6 -> R.drawable.number_6
            7 -> R.drawable.number_7
            8 -> R.drawable.number_8
            9 -> R.drawable.number_9
            else -> R.drawable.number_icon
        }
    }

    private fun getDrawableIdForLetter(letter: Char): Int {
        return when (letter) {
            'A' -> R.drawable.letter_a
            'B' -> R.drawable.letter_b
            'C' -> R.drawable.letter_c
            'D' -> R.drawable.letter_d
            'E' -> R.drawable.letter_e
            'F' -> R.drawable.letter_f
            'G' -> R.drawable.letter_g
            'H' -> R.drawable.letter_h
            'I' -> R.drawable.letter_i
            'J' -> R.drawable.letter_j
            'K' -> R.drawable.letter_k
            'L' -> R.drawable.letter_l
            'M' -> R.drawable.letter_m
            'N' -> R.drawable.letter_n
            'O' -> R.drawable.letter_o
            'P' -> R.drawable.letter_p
            'Q' -> R.drawable.letter_q
            'R' -> R.drawable.letter_r
            'S' -> R.drawable.letter_s
            'T' -> R.drawable.letter_t
            'U' -> R.drawable.letter_u
            'V' -> R.drawable.letter_v
            'W' -> R.drawable.letter_w
            'X' -> R.drawable.letter_x
            'Y' -> R.drawable.letter_y
            'Z' -> R.drawable.letter_z
            else -> R.drawable.default_letter_icon
        }
    }

    private fun navigateToDetailActivity(item: ItemDictionary, position: Int) {
        val intent = Intent(this, DictionaryDetail::class.java)
        intent.putExtra("IMAGE_RES_ID", item.imageResId)
        intent.putExtra("ITEM_INDEX", position)
        intent.putParcelableArrayListExtra("ITEM_LIST", ArrayList(itemList))  // Kirimkan itemList
        startActivity(intent)
    }

}
