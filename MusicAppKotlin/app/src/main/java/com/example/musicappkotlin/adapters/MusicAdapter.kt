package com.example.musicappkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.activities.PlayerActivity
import com.example.musicappkotlin.R
import com.example.musicappkotlin.activities.MainActivity
import com.example.musicappkotlin.activities.PlayListActivity
import com.example.musicappkotlin.activities.PlayListDetails
import com.example.musicappkotlin.databinding.MusicViewBinding
import com.example.musicappkotlin.models.Music
import com.example.musicappkotlin.models.formatDuration

class MusicAdapter(private val context:Context, private var musicList:ArrayList<Music>,
private val playlistDetails: Boolean = false, private val selectionActivity:Boolean = false ) :RecyclerView.Adapter<MusicAdapter.MyHolder>() {

    class MyHolder(binding: MusicViewBinding) :RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
            .into(holder.image)
        when {
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent("PlaylistDetailsAdapter", position)

                }
            }
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if(addSong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_pink))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
            }
            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> sendIntent("MusicAdapterSearch", position)
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent("NowPlaying", PlayerActivity.songPosition)
                        else -> sendIntent("MusicAdapter", position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos:Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ActivityCompat.startActivity(context, intent, null)
    }

    private fun addSong(song: Music):Boolean {
        PlayListActivity.musicPlaylist.ref[PlayListDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id) {
                PlayListActivity.musicPlaylist.ref[PlayListDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlayListActivity.musicPlaylist.ref[PlayListDetails.currentPlaylistPos].playlist.add(song)
        return true
    }
    fun refershPlaylist() {
        musicList = ArrayList()
        musicList = PlayListActivity.musicPlaylist.ref[PlayListDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}