package com.muneeb.musicplayer.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicplayer.ApplicationClass
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.data.setSongPosition
import com.muneeb.musicplayer.ui.activitys.PlayerActivity
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {

            ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = context!!)

            ApplicationClass.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()

            ApplicationClass.NEXT -> prevNextSong(increment = true, context = context!!)

            ApplicationClass.EXIT -> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService!!.mediaPlayer!!.release()
                PlayerActivity.musicService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.binding.btnSongPause.setIconResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play)
        PlayerActivity.binding.btnSongPause.setIconResource(R.drawable.ic_play)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.color.black).centerCrop())
            .into(PlayerActivity.binding.ivSongs)
        PlayerActivity.binding.tvSongsName.text =
            PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
    }

}