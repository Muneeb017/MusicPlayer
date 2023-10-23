package com.muneeb.musicplayer.ui.activitys

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muneeb.musicPlayer.R
import com.muneeb.musicPlayer.databinding.ActivityPlaylistBinding
import com.muneeb.musicPlayer.databinding.DialogPlaylistAddBinding
import com.muneeb.musicplayer.adapters.PlaylistAdapter
import com.muneeb.musicplayer.data.MusicPlaylist
import com.muneeb.musicplayer.data.Playlist
import com.muneeb.musicplayer.data.setDialogBtnBackground
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistAdapter

    companion object {
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])

        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rcvPlaylist.setHasFixedSize(true)
        binding.rcvPlaylist.setItemViewCacheSize(15)
        binding.rcvPlaylist.layoutManager = GridLayoutManager(this, 2)
        adapter = PlaylistAdapter(this, musicPlaylist.ref)
        binding.rcvPlaylist.adapter = adapter
        binding.ivBack.setOnClickListener { finish() }
        binding.btnAdd.setOnClickListener { customAlertDialog() }

    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlaylistActivity)
            .inflate(R.layout.dialog_playlist_add, binding.root, false)
        val binder = DialogPlaylistAddBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        val dialog = builder.setView(customDialog).setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog, _ ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if (playlistName != null && createdBy != null) if (playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                    addPlaylist(playlistName.toString(), createdBy.toString())
                }
                dialog.dismiss()
            }.create()
        dialog.show()
        setDialogBtnBackground(this, dialog)

    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExists = false
        for (i in musicPlaylist.ref) {
            if (name == i.name) {
                playlistExists = true
                break
            }
        }
        if (playlistExists) Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

}