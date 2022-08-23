package com.example.musicappkotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicappkotlin.R
import com.example.musicappkotlin.adapters.FavouritesAdapter
import com.example.musicappkotlin.adapters.PlayListAdapter
import com.example.musicappkotlin.databinding.ActivityPlayListBinding
import com.example.musicappkotlin.databinding.AddPlaylistDialogBinding
import com.example.musicappkotlin.models.MusicPlayList
import com.example.musicappkotlin.models.PlayList
import com.example.musicappkotlin.models.exitApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlayListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayListBinding
    private lateinit var adapter: PlayListAdapter

    companion object {
        var musicPlaylist:MusicPlayList = MusicPlayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackPL.setOnClickListener {
            finish()
        }

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this, 2)
        adapter = PlayListAdapter(this@PlayListActivity, musicPlaylist.ref )
        binding.playlistRV.adapter = adapter
        binding.addPlaylist.setOnClickListener {
            customAlertDialog()
        }

    }

    private fun customAlertDialog() {
        val customDialog = LayoutInflater.from(this@PlayListActivity).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("ADD") { dialog,_, ->
                val playlistName = binder.playlistNametxt.text
                val createdBy = binder.yourNametxt.text
                if(playlistName != null && createdBy != null) {
                    if(playlistName.isNotEmpty() && createdBy.isNotEmpty()) {
                        addPlayList(playlistName.toString(), createdBy.toString())
                    }
                }

                dialog.dismiss()
            }.show()
    }

    private fun addPlayList(name: String, createdBy: String) {
        var playlistExists = false
        for (i in musicPlaylist.ref) {
            if(name.equals(i.name)) {
                playlistExists = true
                break
            }
        }
        if(playlistExists) Toast.makeText(this, "Playlist Exists", Toast.LENGTH_LONG).show()
        else {
            val tempPlayList = PlayList()
            tempPlayList.name = name
            tempPlayList.playlist = ArrayList()
            tempPlayList.createdBy = createdBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlayList.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlayList)
            adapter.refreshPlayList()

        }

    }

    override fun onResume() {
        adapter.notifyDataSetChanged()
        super.onResume()
    }

}