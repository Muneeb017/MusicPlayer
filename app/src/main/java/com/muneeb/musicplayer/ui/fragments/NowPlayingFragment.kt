package com.muneeb.musicplayer.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.data.setSongPosition
import com.muneeb.musicplayer.databinding.FragmentNowPlayingBinding
import com.muneeb.musicplayer.ui.activitys.PlayerActivity

class NowPlayingFragment : Fragment(R.layout.fragment_now_playing) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnNp.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }
        binding.nextBtnNp.setOnClickListener {
            setSongPosition(increment = true)
            PlayerActivity.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.color.black).centerCrop())
                .into(binding.songImgNP)
            binding.songsNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
            playMusic()
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.songsNameNP.isSelected = true
            Glide.with(requireContext())
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_player).centerCrop())
                .into(binding.songImgNP)
            binding.songsNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if (PlayerActivity.isPlaying) binding.playPauseBtnNp.setIconResource(R.drawable.ic_pause)
            else binding.playPauseBtnNp.setIconResource(R.drawable.ic_play)
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNp.setIconResource(R.drawable.ic_pause)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.binding.btnSongNext.setIconResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNp.setIconResource(R.drawable.ic_play)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play)
        PlayerActivity.binding.btnSongNext.setIconResource(R.drawable.ic_play)
    }

}