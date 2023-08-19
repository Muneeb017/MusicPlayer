package com.muneeb.musicplayer.ui.activitys

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.databinding.ActivityPlayerBinding
import com.muneeb.musicplayer.service.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection {

    companion object {
        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
    }

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)

        initializeLayout()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnSongPause.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }

        binding.btnSongPrevious.setOnClickListener {
            prevNextSong(increment = false)
        }

        binding.btnSongNext.setOnClickListener {
            prevNextSong(increment = true)
        }

    }

    private fun setLayout() {
        Glide.with(this).load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.color.black).centerCrop()).into(binding.ivSongs)
        binding.tvSongsName.text = musicListPA[songPosition].title
    }

    private fun createMediaPlayer() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
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

            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createMediaPlayer()
            }
        }
    }

    private fun playMusic() {
        binding.btnSongPause.setIconResource(R.drawable.ic_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnSongPause.setIconResource(R.drawable.ic_play)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
        } else {
            setSongPosition(increment = false)
            setLayout()
        }
    }

    private fun setSongPosition(increment: Boolean) {
        if (increment) {
            if (musicListPA.size - 1 == songPosition) songPosition = 0
            else ++songPosition
        } else {
            if (0 == songPosition) songPosition = musicListPA.size - 1
            else --songPosition
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.showNotification()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

}