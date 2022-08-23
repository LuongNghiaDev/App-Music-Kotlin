package com.example.musicappkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.activities.PlayerActivity
import com.example.musicappkotlin.models.exitApplication
import com.example.musicappkotlin.models.favouriteChecker
import com.example.musicappkotlin.models.setSongPosition
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(false, context = p0!!)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true, context = p0!!)
            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_pause)
        NowFragment.binding.playPauseBtnNP.setIconResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play)
        PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_play)
        NowFragment.binding.playPauseBtnNP.setIconResource(R.drawable.ic_play)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment)
        PlayerActivity.musicService!!.createMediaPlayer()

        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
            .into(PlayerActivity.binding.imageMusicPA)
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
            .into(NowFragment.binding.songImgNP)
        PlayerActivity.binding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFavoutite) PlayerActivity.binding.btnFavourites.setImageResource(R.drawable.ic_favorite)
        else PlayerActivity.binding.btnFavourites.setImageResource(R.drawable.ic_favourites_music)

    }

}