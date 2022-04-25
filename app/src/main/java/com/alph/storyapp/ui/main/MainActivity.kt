package com.alph.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alph.storyapp.R
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.databinding.ActivityMainBinding
import com.alph.storyapp.ui.login.LoginActivity
import com.alph.storyapp.ui.main.adapter.StoryAdapter
import com.alph.storyapp.ui.maps.MapStoryActivity
import com.alph.storyapp.ui.postimage.PostImageActivity
import com.alph.storyapp.utils.Constant.ADD_STORY_RESPONSE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()
    private val storyAdapter: StoryAdapter by lazy { StoryAdapter() }

    private var response: FileUploadResponse? = null

    private val launchAddStory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            response = it.data?.getParcelableExtra(ADD_STORY_RESPONSE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (response != null && response?.error == false) {
                    mainViewModel.getLatestStories()
                }
                mainViewModel.stories.collect {
                    storyAdapter.submitData(it)
                }
            }
        }

        binding.fbAddstory.setOnClickListener{
            val intent = Intent(this, PostImageActivity::class.java)
            launchAddStory.launch(intent)
        }

        binding.rvPhotos.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            R.id.to_map -> {
                val intent = Intent(this@MainActivity, MapStoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }
    }

}