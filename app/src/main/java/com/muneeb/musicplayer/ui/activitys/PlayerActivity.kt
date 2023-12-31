package com.muneeb.musicplayer.ui.activitys

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muneeb.musicPlayer.R
import com.muneeb.musicPlayer.databinding.ActivityPlayerBinding
import com.muneeb.musicPlayer.databinding.AudioBoosterBinding
import com.muneeb.musicPlayer.databinding.BottomSheetMoreBinding
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.data.exitApplication
import com.muneeb.musicplayer.data.favouriteChecker
import com.muneeb.musicplayer.data.formatDuration
import com.muneeb.musicplayer.data.getImgArt
import com.muneeb.musicplayer.data.setDialogBtnBackground
import com.muneeb.musicplayer.data.setSongPosition
import com.muneeb.musicplayer.service.MusicService

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPlayingId: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
        lateinit var loudnessEnhancer: LoudnessEnhancer

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (intent.data?.scheme.contentEquals("content")) {
            val intentService = Intent(this, MusicService::class.java)
            bindService(intentService, this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))
            Glide.with(this)
                .load(getImgArt(musicListPA[songPosition].path))
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(binding.ivSongs)
            binding.tvSongsName.text = musicListPA[songPosition].title
        } else initializeLayout()

        //audio booster feature
        binding.boosterBtnPA.setOnClickListener {
            val customDialogB = LayoutInflater.from(this).inflate(R.layout.audio_booster, binding.root, false)
            val bindingB = AudioBoosterBinding.bind(customDialogB)
            val dialogB = MaterialAlertDialogBuilder(this).setView(customDialogB)
                .setOnCancelListener { playMusic() }
                .setPositiveButton("OK"){self, _ ->
                    loudnessEnhancer.setTargetGain(bindingB.verticalBar.progress * 100)
                    playMusic()
                    self.dismiss()
                }
                .setBackground(ColorDrawable(0xffffff.toInt()))
                .create()
            dialogB.show()

            bindingB.verticalBar.progress = loudnessEnhancer.targetGain.toInt()/100
            bindingB.progressText.text = "Audio Boost\n\n${loudnessEnhancer.targetGain.toInt()/10} %"
            bindingB.verticalBar.setOnProgressChangeListener {
                bindingB.progressText.text = "Audio Boost\n\n${it*10} %"
            }
            setDialogBtnBackground(this, dialogB)
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnSongPause.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else playMusic()
        }
        binding.btnSongPrevious.setOnClickListener {
            prevNextSong(increment = false)
        }
        binding.btnSongNext.setOnClickListener {
            prevNextSong(increment = true)
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService!!.mediaPlayer!!.seekTo(progress)
                    musicService!!.showNotification(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,0F)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        binding.ivMore.setOnClickListener {
            showMoreBottomSheet()
        }

        binding.ivShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
        }

        binding.ivHeart.setOnClickListener {
            if (isFavourite) {
                // Check if the list is not empty before attempting to remove an element
                if (FavouriteActivity.favouriteSongs.isNotEmpty()) {
                    // Ensure fIndex is within the valid range
                    if (fIndex >= 0 && fIndex < FavouriteActivity.favouriteSongs.size) {
                        isFavourite = false
                        binding.ivHeart.setImageResource(R.drawable.ic_favorite_empty)
                        FavouriteActivity.favouriteSongs.removeAt(fIndex)
                    } else {
                        // Handle the case where fIndex is not a valid index
                        // You might want to log or display an error message here
                    }
                }
            } else {
                isFavourite = true
                binding.ivHeart.setImageResource(R.drawable.ic_favorite)
                FavouriteActivity.favouriteSongs.add(musicListPA[songPosition])
            }
        }


    }

    private fun showMoreBottomSheet() {
        val sheet = BottomSheetDialog(this)
        val view = BottomSheetMoreBinding.inflate(layoutInflater)

        view.ivTimer.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheetDialog()
                view.ivTimer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.cool_pink))
            }
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer").setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        view.ivTimer.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        view.ivRepeat.setOnClickListener {
            if (!repeat) {
                repeat = true
                view.ivRepeat.setCardBackgroundColor(ContextCompat.getColor(this, R.color.cool_pink))
            } else {
                repeat = false
                view.ivRepeat.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            }
        }

        view.ivGraphic.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "Equalizer Feature not Supported!!", Toast.LENGTH_SHORT).show()
            }
        }

        sheet.setCancelable(true)
        sheet.setContentView(view.root)
        sheet.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getMusicDetails(contextUri: Uri): Music {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contextUri,projection,null,null,null)
            val dataColum = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColum = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColum?.let { cursor.getString(it) }
            val duration = durationColum?.let { cursor.getLong(it) }!!
            return Music(id = "Unknown", title = path.toString(), album = "Unknown", artist = "Unknown", duration = duration,
                artUri = "Unknown", path = path.toString())
        } finally {
            cursor?.close()
        }
    }

    private fun setLayout() {
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        Glide.with(this).load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music).centerCrop()).into(binding.ivSongs)
        binding.tvSongsName.text = musicListPA[songPosition].title

//        if (repeat) binding.ivRepeat.setColorFilter(
//            ContextCompat.getColor(
//                this, R.color.cool_blue
//            )
//        )

//        if (min15 || min30 || min60) {
//            val view = BottomSheetMoreBinding.inflate(layoutInflater)
//            binding.ivTimer.setColorFilter(
//                ContextCompat.getColor(
//                    this, R.color.cool_blue
//                )
//            )
//        }

        if (isFavourite) binding.ivHeart.setImageResource(R.drawable.ic_favorite)
        else binding.ivHeart.setImageResource(R.drawable.ic_favorite_empty)
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
            musicService!!.showNotification(R.drawable.ic_pause,1F)
            binding.tvTimeStart.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvTimeEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbar.progress = 0
            binding.seekbar.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id
            playMusic()
            loudnessEnhancer = LoudnessEnhancer(musicService!!.mediaPlayer!!.audioSessionId)
            loudnessEnhancer.enabled = true
        } catch (e: Exception) {
            return
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "FavouriteAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                setLayout()
            }

            "NowPlaying" -> {
                setLayout()
                binding.tvTimeStart.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvTimeEnd.text =
                    formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekbar.max = musicService!!.mediaPlayer!!.duration
                if (isPlaying) binding.btnSongPause.setIconResource(R.drawable.ic_pause)
                else binding.btnSongPause.setIconResource(R.drawable.ic_play)
            }

            "MusicAdapterSearch" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }

            "MusicAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
            }

            "MainActivity" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
            }

            "FavouriteShuffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                musicListPA.shuffle()
                setLayout()
            }

            "PlaylistDetailsAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist)
                setLayout()
            }

            "PlaylistDetailsShuffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun playMusic() {
        binding.btnSongPause.setIconResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pause,1F)
        musicService!!.mediaPlayer!!.start()
        isPlaying = true
    }

    private fun pauseMusic() {
        binding.btnSongPause.setIconResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_play,0F)
        musicService!!.mediaPlayer!!.pause()
        isPlaying = false
    }

    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(
            musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        } catch (e: Exception) {
            return
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK) return
    }

    private fun showBottomSheetDialog() {
        val view = BottomSheetDialog(this@PlayerActivity)
        val more = BottomSheetMoreBinding.inflate(layoutInflater)
        view.setContentView(R.layout.bottom_sheet_dialog)
        view.show()

        view.findViewById<CardView>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_SHORT)
                .show()
            min15 = true
            Thread {
                Thread.sleep((15 * 60000).toLong())
                if (min15) exitApplication()
            }.start()
            view.dismiss()
        }

        view.findViewById<CardView>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(this, "Music will stop after 30 minutes", Toast.LENGTH_SHORT).show()
            min30 = true
            Thread {
                Thread.sleep((30 * 60000).toLong())
                if (min30) exitApplication()
            }.start()
            view.dismiss()
        }

        view.findViewById<CardView>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(this, "Music will stop after 60 minutes", Toast.LENGTH_SHORT).show()
            min60 = true
            Thread {
                Thread.sleep((60 * 60000).toLong())
                if (min60) exitApplication()
            }.start()
            view.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicListPA[songPosition].id == "Unknown" && !isPlaying) exitApplication()
    }

}