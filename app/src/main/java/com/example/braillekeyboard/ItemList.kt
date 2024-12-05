package com.example.braillekeyboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ItemList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var itemList: List<Item>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemList = listOf(
            Item("Level 1", "2 Menit       |      3 huruf", R.drawable.play),
            Item("Level 2", "3 Menit       |      4 huruf", R.drawable.play),
            Item("Level 3", "4 Menit       |      5 huruf", R.drawable.play),
            Item("Level 4", "5 Menit       |      6 huruf", R.drawable.play),
            Item("Level 5", "7 Menit       |      8 huruf", R.drawable.play),
            Item("Level 6", "8 Menit       |      Angka", R.drawable.play),
        )

        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}