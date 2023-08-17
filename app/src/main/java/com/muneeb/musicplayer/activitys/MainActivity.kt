package com.muneeb.musicplayer.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.muneeb.musicplayer.R
import com.muneeb.musicplayer.adapters.MusicAdapter
import com.muneeb.musicplayer.data.Music
import com.muneeb.musicplayer.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<Music>
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeLayout()

        binding.btnShuffle.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }

        binding.btnFavourite.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.btnPlaylist.setOnClickListener {
            startActivity(Intent(this, PlaylistActivity::class.java))
        }

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navFeedback -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.navSettings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                R.id.navAbout -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.navExit -> exitProcess(1)
            }
            true
        }
    }

    private fun requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this, "Permission Granted", Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13
            )

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        requestRuntimePermission()
        setTheme(R.style.coolPinkNav)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MusicListMA = getAllAudio()

        binding.rcvSongs.setHasFixedSize(true)
        binding.rcvSongs.setItemViewCacheSize(15)
        binding.rcvSongs.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this, MusicListMA)
        binding.rcvSongs.adapter = musicAdapter
        binding.tvTotalSongs.text = "Total Songs : " + musicAdapter.itemCount

    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) do {
                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val albumIdC =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        .toString()
                val uri = Uri.parse("content://media/external/audio/albumart")
                val artUriC = Uri.withAppendedPath(uri,albumIdC).toString()

                val music = Music(
                    id = idC,
                    title = titleC,
                    album = albumC,
                    artist = artistC,
                    duration = durationC,
                    path = pathC,
                    artUri = artUriC
                )
                val file = File(music.path)
                if (file.exists()) tempList.add(music)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }

}