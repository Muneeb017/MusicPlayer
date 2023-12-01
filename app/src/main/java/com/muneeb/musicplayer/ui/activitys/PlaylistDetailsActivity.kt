package com.muneeb.musicplayer.ui.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.muneeb.musicPlayer.R
import com.muneeb.musicPlayer.databinding.ActivityPlaylistDetailsBinding
import com.muneeb.musicplayer.adapters.MusicAdapter
import com.muneeb.musicplayer.data.checkPlaylist
import com.muneeb.musicplayer.data.setDialogBtnBackground

class PlaylistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        supportActionBar?.hide()

        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.ivBack.setOnClickListener {
//            finish()
//        }

        currentPlaylistPos = intent.extras?.getInt("index") as Int
        try{PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
            checkPlaylist(playlist = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)}
        catch(e: Exception){}
        binding.rcvPlaylistDetails.setHasFixedSize(true)
        binding.rcvPlaylistDetails.setItemViewCacheSize(15)
        binding.rcvPlaylistDetails.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(
            this,
            PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,
            playlistDetails = true
        )
        binding.rcvPlaylistDetails.adapter = musicAdapter

        binding.btnShuffle.setOnClickListener {
            val intent = Intent(this@PlaylistDetailsActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }

        binding.btnAddSongs.setOnClickListener {
            val intent = Intent(this@PlaylistDetailsActivity, SelectionActivity::class.java)
            startActivity(intent)
        }

        binding.btnRemoveAll.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove").setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    musicAdapter.refreshPlaylist()
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
//        binding.playlistName.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfo.text =
            "Total ${musicAdapter.itemCount} Songs. \n\n" + "Create On:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" + " -- ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if (musicAdapter.itemCount > 0) {
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop())
                .into(binding.playlistImg)

            binding.btnShuffle.show()
        }
        musicAdapter.notifyDataSetChanged()
        //for storing favourite data using shared perferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }

}