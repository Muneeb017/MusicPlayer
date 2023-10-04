package com.muneeb.musicplayer.ui.activitys

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.adapters.MusicAdapter
import com.muneeb.musicplayer.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)

        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.get("index") as Int
        binding.rcvPlaylistDetails.setHasFixedSize(true)
        binding.rcvPlaylistDetails.setItemViewCacheSize(15)
        binding.rcvPlaylistDetails.layoutManager = LinearLayoutManager(this)
        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.addAll(MainActivity.MusicListMA)
        musicAdapter = MusicAdapter(
            this,
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,
            playlistDetails = true
        )
        binding.rcvPlaylistDetails.adapter = musicAdapter

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistName.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfo.text =
            "Total ${musicAdapter.itemCount} Songs. \n\n" + "Create On:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" + " -- ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if (musicAdapter.itemCount > 0)
            Glide.with(this)
            .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
            .apply(RequestOptions().placeholder(R.color.black).centerCrop())
            .into(binding.playlistImg)
        binding.btnAdd.show()
    }
}