package com.muneeb.musicplayer.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicPlayer.R
import com.muneeb.musicPlayer.databinding.ItemMusicMainBinding
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.data.formatDuration
import com.muneeb.musicplayer.ui.activitys.MainActivity
import com.muneeb.musicplayer.ui.activitys.PlayerActivity
import com.muneeb.musicplayer.ui.activitys.PlaylistActivity
import com.muneeb.musicplayer.ui.activitys.PlaylistDetailsActivity

class MusicMainAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false
) : RecyclerView.Adapter<MusicMainAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMusicMainBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            ItemMusicMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = musicList[position]

        with(holder.binding) {
            tvSongsName.text = model.title
            tvFolderName.text = model.album
            tvDurationSong.text = formatDuration(model.duration)
            Glide.with(context).load(model.artUri)
                .apply(RequestOptions().placeholder(R.drawable.music).centerCrop()).into(ivSongs)

            when{
                playlistDetails -> {
                    root.setOnClickListener {
                        sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                    }
                }
                selectionActivity ->{
                    root.setOnClickListener {
                        if(addSongs(musicList[position]))
                            root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_pink))
                        else
                            root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    }
                }
                else -> {
                    root.setOnClickListener {
                        when {
                            MainActivity.search -> sendIntent(ref = "MusicAdapter", pos = position)
                            musicList[position].id == PlayerActivity.nowPlayingId -> sendIntent(
                                ref = "NowPlaying", pos = PlayerActivity.songPosition
                            )
                            else -> sendIntent(ref = "MusicAdapter", pos = position)
                        }
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    private fun addSongs(song: Music): Boolean {
        PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.removeAt(
                    index
                )
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetailsActivity.currentPlaylistPos].playlist.add(
            song
        )
        return true
    }

}




