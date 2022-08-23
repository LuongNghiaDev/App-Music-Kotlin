package com.example.musicappkotlin.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.R
import com.example.musicappkotlin.activities.MainActivity
import com.example.musicappkotlin.activities.PlayerActivity
import com.example.musicappkotlin.databinding.FavouritesViewBinding
import com.example.musicappkotlin.databinding.MusicViewBinding
import com.example.musicappkotlin.models.Music
import com.example.musicappkotlin.models.formatDuration

class FavouritesAdapter(private val context: Context, private var favouritesList:ArrayList<Music>) :RecyclerView.Adapter<FavouritesAdapter.MyHolder>() {

    class MyHolder(binding: FavouritesViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesAdapter.MyHolder {
        return MyHolder(FavouritesViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: FavouritesAdapter.MyHolder, position: Int) {
        holder.name.text = favouritesList[position].title
        Glide.with(context)
            .load(favouritesList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavouriteAdapter")
            ActivityCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return favouritesList.size
    }

}