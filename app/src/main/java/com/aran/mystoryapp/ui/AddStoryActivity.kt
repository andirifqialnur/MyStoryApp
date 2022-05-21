package com.aran.mystoryapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aran.mystoryapp.R
import com.aran.mystoryapp.api.ApiConfig
import com.aran.mystoryapp.databinding.ActivityAddStoryBinding
import com.aran.mystoryapp.helper.*
import com.aran.mystoryapp.response.Responses
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryViewModel: SharedViewModel
    private lateinit var binding: ActivityAddStoryBinding

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lat = intent.getFloatExtra(LAT, 1000f)
        val lon = intent.getFloatExtra(LON, 1000f)

        if(lat != 1000f && lon != 1000f) {
            val location = "Latitude = $lat, \nLongitude = $lon"
            binding.location.text = location
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        setupViewModel()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.uploadBtn.setOnClickListener {
            uploadStory()
        }
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(
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
            R.id.menu_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                return true
            }

            R.id.menu_logout -> {
                signOut()
                return true
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        showLoading(true)

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.desc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            addStoryViewModel.getUser().observe(this) {
                if(it != null) {
                    val client = ApiConfig.getApiService().uploadStory("Bearer " + it.token, imageMultipart, description)
                    client.enqueue(object: Callback<Responses> {
                        override fun onResponse(
                            call: Call<Responses>,
                            response: Response<Responses>
                        ) {
                            showLoading(false)
                            val responseBody = response.body()
                            Log.d(TAG, "onResponse: $responseBody")
                            if(response.isSuccessful && responseBody?.message == "Story created successfully") {
                                Toast.makeText(this@AddStoryActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Log.e(TAG, "onFailure1: ${response.message()}")
                                Toast.makeText(this@AddStoryActivity, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Responses>, t: Throwable) {
                            showLoading(false)
                            Log.e(TAG, "onFailure2: ${t.message}" )
                            Toast.makeText(this@AddStoryActivity, getString(R.string.upload_fail), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

        }

    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.img.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.img.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }

    private fun signOut() {
        addStoryViewModel.signOut()
        startActivity(Intent(this, SignInActivity::class.java))
    }

    companion object {
        const val TAG = "AddStoryActivity"
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        const val LAT = "lat"
        const val LON = "lon"
    }
}