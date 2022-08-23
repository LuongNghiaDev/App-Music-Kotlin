package com.example.musicappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicappkotlin.R
import com.example.musicappkotlin.databinding.ActivityAboutBinding
import com.example.musicappkotlin.databinding.ActivitySettingsBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"

        binding.aboutText.text = aboutText()

    }

    private fun aboutText():String {
        return "Developed By: Nghia" +
                "\n\nIf you want to provide feedback, I will love to hear that "
    }
}