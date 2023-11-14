package com.muneeb.musicplayer.ui.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.muneeb.musicPlayer.databinding.ActivityFavouriteBinding
import com.muneeb.musicplayer.adapters.FavouriteAdapter
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.data.checkPlaylist

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object {
        var favouriteSongs: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])

        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Favourites"

        favouriteSongs = checkPlaylist(favouriteSongs)

//        binding.ivBack.setOnClickListener {
//            finish()
//        }

        binding.rcvFavourite.setHasFixedSize(true)
        binding.rcvFavourite.setItemViewCacheSize(15)
        binding.rcvFavourite.layoutManager = GridLayoutManager(this, 4)
        adapter = FavouriteAdapter(this, favouriteSongs)
        binding.rcvFavourite.adapter = adapter
        if (favouriteSongs.size < 1) binding.btnShuffle.hide()

        binding.btnShuffle.setOnClickListener {
            val intent = Intent(this@FavouriteActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }

    }

}