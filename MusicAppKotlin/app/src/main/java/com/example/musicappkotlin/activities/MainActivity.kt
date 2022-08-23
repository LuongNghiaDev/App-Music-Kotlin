package com.example.musicappkotlin.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicappkotlin.R
import com.example.musicappkotlin.adapters.MusicAdapter
import com.example.musicappkotlin.databinding.ActivityMainBinding
import com.example.musicappkotlin.models.Music
import com.example.musicappkotlin.models.MusicPlayList
import com.example.musicappkotlin.models.exitApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA :ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var search: Boolean = false
        var themeIndex:Int = 0
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(R.style.coolPinkNav)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toogle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toogle)
        toogle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission()) {
            init()

            FavouritesActivity.favoutiteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if(jsonString != null) {
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouritesActivity.favoutiteSongs.addAll(data)
            }

            PlayListActivity.musicPlaylist = MusicPlayList()
            val jsonStringPlaylist = editor.getString("MusicPlaylist", null)
            if(jsonStringPlaylist != null) {
                val dataPlaylist: MusicPlayList = GsonBuilder().create().fromJson(jsonStringPlaylist, MusicPlayList::class.java)
                PlayListActivity.musicPlaylist = dataPlaylist
            }
        }

        binding.btnShuffle.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        binding.btnFavourites.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavouritesActivity::class.java))
        }

        binding.btnPlaylists.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlayListActivity::class.java))
        }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navAbout -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                R.id.navFeedback -> startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                R.id.navExit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close app")
                        .setPositiveButton("Yes") { _,_, ->
                            exitApplication()
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
            true
        }
    }

    private fun requestRuntimePermission():Boolean {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 13) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissons granted", Toast.LENGTH_LONG).show()
                init()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toogle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun init() {
        search = false
        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter

        binding.txtTotalSongs.text = "Total Songs: ${musicAdapter.itemCount}"
    }

    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
        MediaStore.Audio.Media.DATE_ADDED + " DESC",null )
        if(cursor != null) {
            if (cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    var uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    val music = Music(idC, titleC, albumC, artistC, durationC, pathC, artUriC)
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)

                } while (cursor.moveToNext())
                cursor.close()
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesActivity.favoutiteSongs)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlayList = GsonBuilder().create().toJson(PlayListActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlayList)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.search_view)?.actionView as SearchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(p0: String?): Boolean {
                musicListSearch = ArrayList()
                if(p0 != null) {
                    val userInput = p0.lowercase()
                    for(song in MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search = true
                    musicAdapter.updateMusicList(musicListSearch)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

}