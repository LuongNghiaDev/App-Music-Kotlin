package com.example.musicappkotlin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicappkotlin.activities.PlayerActivity
import com.example.musicappkotlin.databinding.AddPlaylistDialogBinding
import com.example.musicappkotlin.databinding.FragmentNowBinding
import com.example.musicappkotlin.models.setSongPosition

class NowFragment : Fragment() {

    companion object {
        lateinit var binding:FragmentNowBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now, container, false)
        binding = FragmentNowBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.songNameNP.isSelected = true

        binding.playPauseBtnNP.setOnClickListener {
            if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
        }

        binding.nextBtnNP.setOnClickListener {
            setSongPosition(true)
            PlayerActivity.musicService!!.createMediaPlayer()

            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
                .into(NowFragment.binding.songImgNP)
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
            playMusic()
        }

        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            ActivityCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.musicService != null) {
            binding.root.visibility = View.VISIBLE
            Glide.with(this)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash_scrreen).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            if(PlayerActivity.isPlaying) binding.playPauseBtnNP.setIconResource(R.drawable.ic_pause)
            else binding.playPauseBtnNP.setIconResource(R.drawable.ic_pause)
        }
    }

    private fun playMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.ic_pause)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_pause)
        PlayerActivity.isPlaying = true
    }

    private fun pauseMusic() {
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.ic_play)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play)
        PlayerActivity.binding.btnPausePA.setIconResource(R.drawable.ic_play)
        PlayerActivity.isPlaying = false
    }
}