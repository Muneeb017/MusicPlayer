package com.muneeb.musicplayer.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
          finish()
        }

    }
}