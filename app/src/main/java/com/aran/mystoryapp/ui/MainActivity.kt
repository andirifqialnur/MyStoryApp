package com.aran.mystoryapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aran.mystoryapp.R
import androidx.activity.viewModels
import com.aran.mystoryapp.adapter.LoadingStateAdapter
import com.aran.mystoryapp.adapter.StoryAdapter
import com.aran.mystoryapp.databinding.ActivityMainBinding
import com.aran.mystoryapp.helper.SharedViewModel
import com.aran.mystoryapp.helper.UserPreference
import com.aran.mystoryapp.helper.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private val storyViewModel: StoryViewModel by viewModels {
        StoryViewModel.ViewModelFactory(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        val layoutManager = LinearLayoutManager(this)
        binding.recycleview.layoutManager = layoutManager

        getStories()

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setupViewModel() {
        sharedViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SharedViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_map -> {
                startActivity(Intent(this, StoryMapsActivity::class.java))
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }

            R.id.menu_logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("Do you want to Sign Out?")
                    .setPositiveButton("Yes"){_, _ ->
                        signOut()}
                    .setNegativeButton("No"){_,_->}
                    .show()
            }
        }
        return true
    }

    private fun getStories() {
        val adapter = StoryAdapter()
        binding.recycleview.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        sharedViewModel.getUser().observe(this) { userAuth ->
            if(userAuth != null) {
                storyViewModel.stories("Bearer " + userAuth.token).observe(this) { stories ->
                    adapter.submitData(lifecycle, stories)
                }
            }
        }
    }

    private fun signOut() {
        sharedViewModel.signOut()
        startActivity(Intent(this, SignInActivity::class.java))
    }
}