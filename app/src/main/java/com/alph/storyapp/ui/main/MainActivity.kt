package com.alph.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alph.storyapp.databinding.ActivityMainBinding
import com.alph.storyapp.storage.UserPreference
import com.alph.storyapp.ui.ViewModelFactory
import com.alph.storyapp.ui.login.LoginActivity
import com.alph.storyapp.ui.main.adapter.StoryAdapter

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

        mainViewModel.getStories().observe(this, {
            if (it != null) {
                adapter.setStoryList(it)
                adapter.notifyDataSetChanged()
                showLoading(false)
            } else {
                showLoading(false)
                Toast.makeText(this, "Error getting list or list is null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }

}