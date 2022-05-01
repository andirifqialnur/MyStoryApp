package com.aran.mystoryapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aran.mystoryapp.R
import com.aran.mystoryapp.adapter.StoryAdapter
import com.aran.mystoryapp.api.ApiConfig
import com.aran.mystoryapp.databinding.ActivityMainBinding
import com.aran.mystoryapp.helper.Helper
import com.aran.mystoryapp.helper.SharedViewModel
import com.aran.mystoryapp.helper.UserPreference
import com.aran.mystoryapp.helper.ViewModelFactory
import com.aran.mystoryapp.model.StoryModel
import com.aran.mystoryapp.response.ListStoryItem
import com.aran.mystoryapp.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var storyViewModel: SharedViewModel
    private lateinit var help: Helper
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        help = Helper(this)

        setupViewModel()

        val layoutManager = LinearLayoutManager(this)
        binding.recycleview.layoutManager = layoutManager

        getStories()

        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setupViewModel() {
        storyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SharedViewModel::class.java]

        storyViewModel.getUser().observe(this) { user ->
            if(user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
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
        showLoading(true)

        storyViewModel.getUser().observe(this ) {
            if(it != null) {
                val client = ApiConfig.getApiService().getStories("Bearer " + it.token)
                client.enqueue(object: Callback<StoriesResponse> {
                    override fun onResponse(
                        call: Call<StoriesResponse>,
                        response: Response<StoriesResponse>
                    ) {
                        showLoading(false)
                        val responseBody = response.body()
                        Log.d(TAG, "onResponse: $responseBody")
                        if(response.isSuccessful && responseBody?.message == "Stories fetched successfully") {
                            setStoriesData(responseBody.listStory)
                            Toast.makeText(this@MainActivity, getString(R.string.success_load_stories), Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e(TAG, "onFailure1: ${response.message()}")
                            Toast.makeText(this@MainActivity, getString(R.string.fail_load_stories), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                        showLoading(false)
                        Log.e(TAG, "onFailure2: ${t.message}")
                        Toast.makeText(this@MainActivity, getString(R.string.fail_load_stories), Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun setStoriesData(items: List<ListStoryItem>) {
        val listStories = ArrayList<StoryModel>()
        for(item in items) {
            val story = StoryModel(
                item.name,
                item.photoUrl,
                item.description,
                item.createdAt
            )
            listStories.add(story)
        }

        val adapter = StoryAdapter(listStories)
        binding.recycleview.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun signOut() {
        help.clear()
        startActivity(Intent(this, SignInActivity::class.java))
        Toast.makeText(applicationContext, "Logged Out", Toast.LENGTH_LONG).show()
        finish()
    }

    companion object {
        private const val TAG = "Story Activity"
    }
}