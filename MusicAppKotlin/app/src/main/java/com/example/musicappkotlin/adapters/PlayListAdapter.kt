package com.example.musicappkotlin.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.R
import com.example.musicappkotlin.activities.PlayListActivity
import com.example.musicappkotlin.activities.PlayListDetails
import com.example.musicappkotlin.databinding.PlaylistViewBinding
import com.example.musicappkotlin.models.PlayList
import com.example.musicappkotlin.models.exitApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayListAdapter(private val context: Context, private var playList:ArrayList<PlayList>) :RecyclerView.Adapter<PlayListAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root = binding.root
        val delete = binding.btnDeletePlaylist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListAdapter.MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: PlayListAdapter.MyHolder, position: Int) {
        holder.name.text = playList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playList[position].name)
                .setMessage("Do you want to delete playlist")
                .setPositiveButton("Yes") { dialog,_, ->
                    PlayListActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlayList()
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
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayListDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if(PlayListActivity.musicPlaylist.ref[position].playlist.size > 0) {
            Glide.with(context)
                .load(PlayListActivity.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
                .into(holder.image)
        }

    }

    override fun getItemCount(): Int {
        return playList.size
    }
    fun refreshPlayList() {
        playList = ArrayList()
        playList.addAll(PlayListActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}