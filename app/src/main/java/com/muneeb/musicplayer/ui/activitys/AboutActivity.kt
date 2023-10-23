package com.muneeb.musicplayer.ui.activitys

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muneeb.musicPlayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.aboutText.text =
            "Developed By: Muneeb Khalid" + "\n\nIf you want to provide feedback, \nI will love to hear that."
    }

}