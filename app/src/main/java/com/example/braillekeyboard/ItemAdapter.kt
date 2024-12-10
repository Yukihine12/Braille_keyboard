package com.example.braillekeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.util.logging.Handler

class ItemAdapter(private val itemList: List<Item>, private val tts: TextToSpeech) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val handler = android.os.Handler()
    private var lastClickTime: Long = 0
    private val doubleClickThreshold: Long = 300 // Waktu dalam milidetik untuk deteksi klik ganda

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.name
        holder.itemDescription.text = item.description
        holder.itemImage.setImageResource(item.imageResourceId)

        // Set OnTouchListener to differentiate between single and double clicks
        holder.itemView.setOnClickListener { view ->
            val currentTime = System.currentTimeMillis()
            val vibrator = view.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            // Check if the click is within the double-click threshold
            if (currentTime - lastClickTime < doubleClickThreshold) {
                // Double-click detected, navigate to DetailActivity
                val context = holder.itemView.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("title", item.name)
                    putExtra("description", item.description)
                    putExtra("imageResId", item.imageResourceId)
                }
                context.startActivity(intent)
            } else {
                // Single-click detected, use TTS to read the item name
                tts.speak(item.name, TextToSpeech.QUEUE_FLUSH, null, null)
            }

            // Update the last click time to the current time
            lastClickTime = currentTime
        }
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