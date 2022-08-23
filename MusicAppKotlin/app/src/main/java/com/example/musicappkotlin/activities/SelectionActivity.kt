package com.example.musicappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicappkotlin.R
import com.example.musicappkotlin.adapters.MusicAdapter
import com.example.musicappkotlin.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackSL.setOnClickListener {
            finish()
        }

        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, MainActivity.MusicListMA, selectionActivity = true)
        binding.selectionRV.adapter = adapter

        binding.searchSelection.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(p0: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(p0 != null) {
                    val userInput = p0.lowercase()
                    for(song in MainActivity.MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(MainActivity.musicListSearch)
                }
                return true
            }

        })

    }
}