package com.digitaldream.linkskool.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.models.ImageGallery
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import java.io.File

class ImageGalleryDialogFragment(
    private val onSelected: (String, String) -> Unit
) : DialogFragment(R.layout.fragment_image_gallery_dialog) {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var navigateBackButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog2)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    loadAllImages()
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)
        navigateBackButton = view.findViewById(R.id.navigateUp)

        navigateBackButton.setOnClickListener {
            dismiss()
        }

        checkForPermission()
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission
                        .READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                loadAllImages()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission
                        .READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                loadAllImages()
            }
        }
    }


    private fun loadAllImages() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        val imageList = mutableListOf<ImageGallery>()

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val data = it.getString(dataColumn)

                imageList.add(ImageGallery(imageId = "$id", imageName = name, imageData = data))
            }
        }

        setUpImageAdapter(imageList)
    }

    private fun setUpImageAdapter(imageList: MutableList<ImageGallery>) {
        val picasso = Picasso.get()

        val imageAdapter = GenericAdapter2(
            itemResLayout = R.layout.item_image_gallery,
            itemList = imageList,
            bindItem = { itemView, model, _ ->
                val imageView: ImageView = itemView.findViewById(R.id.imageView)

                val imageFile = File(model.imageData)
                picasso.load(imageFile).into(imageView)

                itemView.setOnClickListener {
                    onSelected.invoke(model.imageName, model.imageData)
                    dismiss()
                }
            }
        )

        imageRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = imageAdapter
        }
    }

}