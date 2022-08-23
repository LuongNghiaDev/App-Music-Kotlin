package com.example.musicappkotlin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicappkotlin.R
import com.example.musicappkotlin.adapters.FavouritesAdapter
import com.example.musicappkotlin.adapters.MusicAdapter
import com.example.musicappkotlin.databinding.ActivityFavouritesBinding
import com.example.musicappkotlin.models.Music
import com.example.musicappkotlin.models.checkPlaylist

class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private lateinit var favouritesAdapter: FavouritesAdapter

    companion object {
        var favoutiteSongs: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favoutiteSongs = checkPlaylist(favoutiteSongs)
        binding.btnBackFA.setOnClickListener {
            finish()
        }

        binding.favoutitesRV.setHasFixedSize(true)
        binding.favoutitesRV.setItemViewCacheSize(13)
        binding.favoutitesRV.layoutManager = GridLayoutManager(this, 4)
        favouritesAdapter = FavouritesAdapter(this@FavouritesActivity, favoutiteSongs )
        binding.favoutitesRV.adapter = favouritesAdapter

        if(favoutiteSongs.size < 1) binding.btnShuffleFA.visibility = View.INVISIBLE
        binding.btnShuffleFA.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            ActivityCompat.startActivity(this, intent, null)
        }
    }
}