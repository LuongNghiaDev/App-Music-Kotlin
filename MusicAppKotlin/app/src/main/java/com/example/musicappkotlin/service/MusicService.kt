package com.example.musicappkotlin.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.musicappkotlin.ApplicationClass
import com.example.musicappkotlin.NotificationReceiver
import com.example.musicappkotlin.NowFragment
import com.example.musicappkotlin.R
import com.example.musicappkotlin.activities.MainActivity
import com.example.musicappkotlin.activities.PlayerActivity
import com.example.musicappkotlin.models.formatDuration
import com.example.musicappkotlin.models.getImgArt

class MusicService: Service(), AudioManager.OnAudioFocusChangeListener {

    private var myBinder = MyMyBinder()
    var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(p0: Intent?): IBinder? {
            mediaSession = MediaSessionCompat(baseContext, "My Music")
            return myBinder
    }

    inner class MyMyBinder: Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    fun showNotification(playPauseBtn :Int) {
        val intent = Intent(baseContext, MainActivity::class.java)
        val contextIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevpendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playpendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextpendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitpendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.splash_scrreen)
        }

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contextIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_previous, "Previous", prevpendingIntent)
            .addAction(playPauseBtn, "Play", playpendingIntent)
            .addAction(R.drawable.ic_skip_next, "Next", nextpendingIntent)
            .addAction(R.drawable.ic_exit, "Exit", exitpendingIntent)
            .build()

        startForeground(13, notification)
    }

    fun createMediaPlayer() {
        try {
            if(PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_pause)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
            PlayerActivity.binding.seekBarStartPA.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarEndPA.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mediaPlayer!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id

        }catch (e:Exception) {return}
    }

    fun seekBarSetup() {
        runnable = Runnable {
            PlayerActivity.binding.seekBarStartPA.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onAudioFocusChange(p0: Int) {
        if(p0 <= 0) {
            PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_play)
            NowFragment.binding.playPauseBtnNP.setIconResource(R.drawable.ic_play)
            showNotification(R.drawable.ic_play)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()
        } else {
            PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_pause)
            NowFragment.binding.playPauseBtnNP.setIconResource(R.drawable.ic_pause)
            showNotification(R.drawable.ic_pause)
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
        }
    }


}