package com.example.musicappkotlin.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.NowFragment
import com.example.musicappkotlin.R
import com.example.musicappkotlin.databinding.ActivityPlayerBinding
import com.example.musicappkotlin.databinding.BottomSheetDialogBinding
import com.example.musicappkotlin.models.*
import com.example.musicappkotlin.service.MusicService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicListPA: ArrayList<Music>
        var songPosition: Int = 0
        var isPlaying:Boolean = false
        var musicService: MusicService? = null
        lateinit var binding:ActivityPlayerBinding
        var repeat: Boolean = false
        var min15:Boolean = false
        var min30:Boolean = false
        var min60:Boolean = false
        var nowPlayingId:String = ""
        var isFavoutite:Boolean = false
        var fIndex:Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMusic()

        binding.btnBackPA.setOnClickListener {
            finish()
        }

        binding.btnPausePA.setOnClickListener {
            if(isPlaying) pauseMusic()
            else playMusic()
        }
        binding.btnPrevious.setOnClickListener {
            prevNextSong(false)
        }
        binding.btnNext.setOnClickListener {
            prevNextSong(true)
        }
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                if(p2) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })

        binding.btnRepeatPA.setOnClickListener {
            if(!repeat) {
                repeat = true
                binding.btnRepeatPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                repeat = false
                binding.btnRepeatPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }

        binding.btnGraphicPA.setOnClickListener {
            try {
                val EqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                EqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                EqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                EqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(EqIntent, 13)

            }catch (e:Exception) {
                Toast.makeText(this, "Equalizer Fearture not support", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnTimerPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if(!timer)  showBottomSheetDialog()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer")
                    .setPositiveButton("Yes") { _,_, ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.btnTimerPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
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

        binding.btnSharePA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Sharing music file"))
        }

        binding.btnFavourites.setOnClickListener {
            if(isFavoutite) {
                isFavoutite = false
                binding.btnFavourites.setImageResource(R.drawable.ic_favourites_music)
                FavouritesActivity.favoutiteSongs.removeAt(fIndex)
            } else {
                isFavoutite = true
                binding.btnFavourites.setImageResource(R.drawable.ic_favorite)
                FavouritesActivity.favoutiteSongs.add(musicListPA[songPosition])
            }
        }

    }

    private fun setLayout() {
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
            .into(binding.imageMusicPA)
        binding.songNamePA.text = musicListPA[songPosition].title
        if(repeat) binding.btnRepeatPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        if(min15 || min30 || min60) binding.btnTimerPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        if(isFavoutite) binding.btnFavourites.setImageResource(R.drawable.ic_favorite)
        else binding.btnFavourites.setImageResource(R.drawable.ic_favourites_music)

    }

    private fun createMediaPlayer() {
        try {
            if(musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.btnPausePA.setIconResource(R.drawable.ic_pause)
            musicService!!.showNotification(R.drawable.ic_pause)
            binding.seekBarStartPA.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.seekBarEndPA.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songPosition].id

        }catch (e:Exception) {return}
    }

    private fun initMusic() {
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "FavouriteAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavouritesActivity.favoutiteSongs)
                setLayout()
            }
            "NowPlaying" -> {
                setLayout()
                binding.seekBarStartPA.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.seekBarEndPA.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
                if(isPlaying) binding.btnPausePA.setIconResource(R.drawable.ic_pause)
                else binding.btnPausePA.setIconResource(R.drawable.ic_play)
            }
            "MusicAdapterSearch" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                //for start service
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
            }
            "FavouriteShuffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(FavouritesActivity.favoutiteSongs)
                musicListPA.shuffle()
                setLayout()
            }
            "PlaylistDetailsAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlayListActivity.musicPlaylist.ref[PlayListDetails.currentPlaylistPos].playlist)
                setLayout()
            }
            "PlayListDetailsShuffle" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlayListActivity.musicPlaylist.ref[PlayListDetails.currentPlaylistPos].playlist)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun playMusic() {
        binding.btnPausePA.setIconResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pause)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        binding.btnPausePA.setIconResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_play)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun prevNextSong(increment: Boolean) {
        if(increment) {
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyMyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {setLayout()}catch (e:Exception) {return}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode == RESULT_OK) {
            return
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min15)?.setOnClickListener {
            Toast.makeText(baseContext, "Min 15", Toast.LENGTH_LONG).show()
            binding.btnTimerPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min15 = true
            Thread {
                Thread.sleep((15 * 60000).toLong())
                if(min15)
                    exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min30)?.setOnClickListener {
            Toast.makeText(baseContext, "Min 30", Toast.LENGTH_LONG).show()
            binding.btnTimerPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min30 = true
            Thread {
                Thread.sleep((30 * 60000).toLong())
                if(min30)
                    exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min60)?.setOnClickListener {
            Toast.makeText(baseContext, "Min 60", Toast.LENGTH_LONG).show()
            binding.btnTimerPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min60 = true
            Thread {
                Thread.sleep((60 * 60000).toLong())
                if(min60)
                    exitApplication()
            }.start()
            dialog.dismiss()
        }
    }

}