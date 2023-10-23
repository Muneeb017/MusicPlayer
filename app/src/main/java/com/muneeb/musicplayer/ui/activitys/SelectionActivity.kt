package com.muneeb.musicplayer.ui.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.muneeb.musicPlayer.databinding.ActivitySelectionBinding
import com.muneeb.musicplayer.adapters.MusicAdapter

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])

        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.rcvSelection.setHasFixedSize(true)
        binding.rcvSelection.setItemViewCacheSize(15)
        binding.rcvSelection.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true)
        binding.rcvSelection.adapter = musicAdapter

        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA) if (song.title.lowercase()
                            .contains(userInput)
                    ) MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    musicAdapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })

    }

}