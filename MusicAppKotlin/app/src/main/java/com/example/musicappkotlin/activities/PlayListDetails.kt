package com.example.musicappkotlin.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.R
import com.example.musicappkotlin.adapters.MusicAdapter
import com.example.musicappkotlin.databinding.ActivityPlayListDetailsBinding
import com.example.musicappkotlin.models.MusicPlayList
import com.example.musicappkotlin.models.checkPlaylist
import com.example.musicappkotlin.models.exitApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlayListDetails : AppCompatActivity() {

    private lateinit var binding:ActivityPlayListDetailsBinding
    lateinit var adapter:MusicAdapter

    companion object {
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPos = intent.extras?.get("index") as Int
        PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist =
            checkPlaylist(PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist)

        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter

        binding.btnBackPD.setOnClickListener {
            finish()
        }

        binding.btnShuffleBtnPD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlayListDetailsShuffle")
            ActivityCompat.startActivity(this, intent, null)
        }
        binding.btnAddPD.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }

        binding.btnRemoveAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs for playlist")
                .setPositiveButton("Yes") { dialog,_, ->
                    PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refershPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlayListActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlayListActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                " -- ${PlayListActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount > 0) {
            Glide.with(this)
                .load(PlayListActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
                .into(binding.playlistImgPD)
            binding.btnShuffleBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()

        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlayList = GsonBuilder().create().toJson(PlayListActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlayList)
        editor.apply()
    }

}