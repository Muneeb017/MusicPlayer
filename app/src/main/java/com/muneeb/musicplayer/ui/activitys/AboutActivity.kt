package com.muneeb.musicplayer.ui.activitys

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muneeb.musicPlayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.aboutTextName.text = "Developed By:  Muneeb Khalid"

        binding.aboutText.text =
            "If you want to provide feedback, \n\nWhen you think about putting words to music, it's usually in the context of writing lyrics for songs. But in the past few years, music technology has been shifting in a way that will give language a whole new relationship to sound.\n" + "\n" + "Text to music software lets users generate music (and other unexpected sounds) from freeform text descriptions, so that everyday thoughts can literally become music. You ability to use natural language to describe a mood, rhythm, styles and features of a song idea will soon be all that's necessary to create an original composition or hit song. Even non-musical ideas, like the name of a movie character or the things you desire, can be used for music generation if you employ audio ciphers and musical cryptograms.  \n" + "\n" + "In this article we'll review the best text to music software available today, giving you the resources you'll need to begin exploring this interesting new landscape for yourself.\n" + "\n" + "Thank you for choosing our music player app! We are dedicated to providing you with a seamless and enjoyable music listening experience. If you have any feedback or suggestions, feel free to reach out to us at support  muneebkhalid0017@gmail.com\n" + "\n" + "Enjoy the music!"
    }

}