package com.muneeb.musicplayer.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.data.formatDuration
import com.muneeb.musicplayer.databinding.ItemMusicBinding
import com.muneeb.musicplayer.ui.activitys.MainActivity
import com.muneeb.musicplayer.ui.activitys.PlayerActivity

class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private var playlistDetails: Boolean = false
) : RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {

        val title = binding.tvSongsName
        val album = binding.tvFolderName
        val image = binding.ivSongs
        val duration = binding.tvDurationSong
        val root = binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        holder.title.text = musicList[position].title

        holder.album.text = musicList[position].album

        holder.duration.text = formatDuration(musicList[position].duration)

        Glide.with(context).load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.color.black).centerCrop()).into(holder.image)

        holder.root.setOnClickListener {
            when {
                MainActivity.search -> sendIntent(ref = "MusicAdapter", pos = position)
                musicList[position].id == PlayerActivity.nowPlayingId -> sendIntent(
                    ref = "NowPlaying", pos = PlayerActivity.songPosition
                )

                else -> sendIntent(ref = "MusicAdapter", pos = position)
            }
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

}




