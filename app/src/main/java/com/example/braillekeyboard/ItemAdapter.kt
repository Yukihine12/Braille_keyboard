package com.example.braillekeyboard

import android.content.Intent
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        holder.itemImage.setImageResource(item.imageResourceId)

        // Periksa aksesibilitas level
        val context = holder.itemView.context
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putInt("current_score", 0).apply()
        val isLevelAccessible = sharedPreferences.getBoolean("level_$position", position == 0) // Default: hanya level 0 (Introduction) terbuka

        holder.itemView.isEnabled = isLevelAccessible // Nonaktifkan item jika tidak dapat diakses
        holder.itemView.alpha = if (isLevelAccessible) 1.0f else 0.5f // Transparansi untuk level terkunci

        // Set OnClickListener untuk setiap item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            if (isLevelAccessible) {
            // Pilih aktivitas berdasarkan posisi
            val targetActivity = when (position) {
                0 -> IntroductionActivity::class.java
                1 -> Level1Activity::class.java
                2 -> Level2Activity::class.java
                3 -> Level3Activity::class.java
                4 -> Level4Activity::class.java
                5 -> Level5Activity::class.java
                6 -> Level6Activity::class.java
                else -> null
            }
                targetActivity?.let {
                    context.startActivity(Intent(context, it))
                }
            } else {
                Toast.makeText(context, "Level terkunci. Selesaikan level sebelumnya.", Toast.LENGTH_SHORT).show()
                // Jika tidak ada level, buka DetailActivity sebagai default
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("title", item.name)
                    putExtra("description", item.description)
                    putExtra("imageResId", item.imageResourceId)
                }
                context.startActivity(intent)
            }
        }
    }

    fun refreshData() {
        notifyDataSetChanged() // Beritahu adapter untuk memperbarui semua item
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDescription: TextView = itemView.findViewById(R.id.itemDescription)
    }
}
