package com.muneeb.musicplayer.ui.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Setting"

        binding.ivBack.setOnClickListener {
            finish()
        }

    }
}