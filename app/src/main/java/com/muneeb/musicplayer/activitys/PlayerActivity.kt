package com.muneeb.musicplayer.activitys

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeLayout()

        binding.btnSongPause.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun setLayout() {
        Glide.with(this).load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.color.black).centerCrop()).into(binding.ivSongs)

        binding.tvSongsName.text = musicListPA[songPosition].title
    }

    private fun createMediaPlayer() {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            isPlaying = true
            binding.btnSongPause.setIconResource(R.drawable.ic_pause)
        } catch (e: Exception) {
            return
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
                createMediaPlayer()
            }
        }
    }

    private fun playMusic() {
        binding.btnSongPause.setIconResource(R.drawable.ic_pause)
        isPlaying = true
        mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnSongPause.setIconResource(R.drawable.ic_play)
        isPlaying = false
        mediaPlayer!!.pause()
    }

}