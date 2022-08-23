package com.example.musicappkotlin.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicappkotlin.R
import com.example.musicappkotlin.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"

    }

}

