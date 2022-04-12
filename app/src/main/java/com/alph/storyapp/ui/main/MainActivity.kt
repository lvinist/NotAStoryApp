package com.alph.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alph.storyapp.R
import com.alph.storyapp.data.Story
import com.alph.storyapp.databinding.ActivityMainBinding
import com.alph.storyapp.storage.UserPreference
import com.alph.storyapp.ui.ViewModelFactory
import com.alph.storyapp.ui.detail.StoryDetailActivity
import com.alph.storyapp.ui.login.LoginActivity
import com.alph.storyapp.ui.main.adapter.StoryAdapter
import com.alph.storyapp.ui.postimage.PostImageActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        setupViewModel()

        binding.fbAddstory.setOnClickListener{
            val intent = Intent(this@MainActivity, PostImageActivity::class.java)
            startActivity(intent)
        }

        adapter.setOnItemClickCallback(object: StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                val intent = Intent(this@MainActivity, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.EXTRA_STORY, data)
                startActivity(intent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                mainViewModel.logout()
                finish()
                true
            }
            else -> true
        }
    }

    private fun initRecyclerView() {
        binding.rvPhotos.layoutManager = LinearLayoutManager(this)
        adapter = StoryAdapter()
        binding.rvPhotos.adapter = adapter
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            showLoading(true)
            mainViewModel.setStories(tokenAuth = user.token)
        }

        mainViewModel.getStories().observe(this) {
            if (it != null) {
                adapter.setStoryList(it)
                adapter.notifyDataSetChanged()
                showLoading(false)
            } else {
                showLoading(false)
                binding.tvNullData.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }

}