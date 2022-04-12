package com.alph.storyapp.ui.postimage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.alph.storyapp.R
import com.alph.storyapp.api.ApiConfig
import com.alph.storyapp.data.FileUploadResponse
import com.alph.storyapp.databinding.ActivityPostImageBinding
import com.alph.storyapp.storage.UserPreference
import com.alph.storyapp.ui.ViewModelFactory
import com.alph.storyapp.ui.main.MainViewModel
import com.alph.storyapp.utils.reduceFileImage
import com.alph.storyapp.utils.uriToFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
class PostImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostImageBinding
    private lateinit var viewModel: MainViewModel

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_no_granted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        setupViewModel()

        binding.btCamera.setOnClickListener { startCameraX() }
        binding.btGallery.setOnClickListener { startGallery() }
        binding.btUpload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) { uploadImage() } }
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostImageActivity)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            binding.btUpload.visibility = View.GONE
            binding.pbPost.visibility = View.VISIBLE

            viewModel.getUser().observe(this) { user ->
                val service = ApiConfig().getApiService().uploadImage(imageMultipart, description, "Bearer ${user.token}")
                service.enqueue(object : Callback<FileUploadResponse> {
                    override fun onResponse(
                        call: Call<FileUploadResponse>,
                        response: Response<FileUploadResponse>
                    ){
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                Toast.makeText(this@PostImageActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@PostImageActivity, response.message(), Toast.LENGTH_SHORT).show()
                            binding.btUpload.visibility = View.VISIBLE
                        }
                    }
                    override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                        Toast.makeText(this@PostImageActivity, getString(R.string.failed_retrofit_instance), Toast.LENGTH_SHORT).show()
                        binding.btUpload.visibility = View.VISIBLE
                    }
                })
            }

        } else {
            Toast.makeText(this@PostImageActivity, getString(R.string.add_image_warning), Toast.LENGTH_SHORT).show()
        }
    }

}