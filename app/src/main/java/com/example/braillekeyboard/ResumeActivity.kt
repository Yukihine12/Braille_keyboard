package com.example.braillekeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ResumeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Muat level terakhir
        val lastLevel = loadLastLevel()

        // Arahkan pemain ke aktivitas level yang sesuai
        when (lastLevel) {
            1 -> navigateToLevel(Level1Activity::class.java)
            2 -> navigateToLevel(Level2Activity::class.java)
            3 -> navigateToLevel(Level3Activity::class.java)
            4 -> navigateToLevel(Level4Activity::class.java)
            5 -> navigateToLevel(Level5Activity::class.java)
            6 -> navigateToLevel(Level6Activity::class.java)
            else -> navigateToLevel(IntroductionActivity::class.java) // Default ke Level 1
        }
    }

    private fun loadLastLevel(): Int {
        val sharedPreferences = getSharedPreferences("BrailleKeyboardPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("last_level", 0) // Default ke level 1 jika tidak ada data
    }

    private fun navigateToLevel(levelActivity: Class<*>) {
        val intent = Intent(this, levelActivity)
        startActivity(intent)
        finish() // Tutup ResumeActivity setelah navigasi
    }
}
