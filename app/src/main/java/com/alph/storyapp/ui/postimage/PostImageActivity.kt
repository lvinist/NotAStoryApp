package com.alph.storyapp.ui.postimage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alph.storyapp.R
import com.alph.storyapp.databinding.ActivityPostImageBinding
import com.alph.storyapp.utils.Constant.ADD_STORY_RESPONSE
import com.alph.storyapp.utils.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PostImageActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostImageBinding
    private val viewModel: PostImageViewModel by viewModels()

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

        binding.btCamera.setOnClickListener { startCameraX() }
        binding.btGallery.setOnClickListener { startGallery() }

        viewModel.image.observe(this) { file ->
            binding.apply {
                previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
                btUpload.isEnabled = file != null
                btUpload.setOnClickListener {
                    val description = binding.edtDescription.text.toString()
                    viewModel.addNewStoryWithToken(
                        description,
                        file
                    )
                }
            }
        }

        viewModel.addStoryResponse.observe(this@PostImageActivity) {
            if (it.error == false && it != null) {
                setResult(RESULT_OK, Intent().putExtra(ADD_STORY_RESPONSE, it))
                finishAndRemoveTask()
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostImageActivity)

            viewModel.saveImage(myFile)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            viewModel.saveImage(myFile)
            it.data?.getBooleanExtra("isBackCamera", true) as Boolean
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

}