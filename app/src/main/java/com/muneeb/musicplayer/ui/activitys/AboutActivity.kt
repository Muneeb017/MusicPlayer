package com.muneeb.musicplayer.ui.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

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