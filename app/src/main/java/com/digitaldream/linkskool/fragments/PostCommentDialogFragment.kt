package com.digitaldream.linkskool.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.dialog.ImageGalleryDialogFragment
import com.digitaldream.linkskool.models.ImageGallery
import com.squareup.picasso.Picasso
import java.io.File


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PostCommentDialogFragment : DialogFragment(R.layout.fragment_post_comment_dialog) {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val picasso = Picasso.get()

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var navigateBackButton: ImageView
    private lateinit var selectedImageView: ImageView
    private lateinit var imageContainer: CardView
    private lateinit var removeImageButton: ImageButton

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog2)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    loadAllImages()
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialiseViews(view)

        handleViewClicks()

        initData()

    }


    private fun initialiseViews(view: View) {
        view.apply {
            imageRecyclerView = findViewById(R.id.imageRecyclerView)
            navigateBackButton = findViewById(R.id.navigateUp)
            selectedImageView = findViewById(R.id.selectedImageView)
            imageContainer = findViewById(R.id.imageContainer)
            removeImageButton = findViewById(R.id.removeImageButton)
        }
    }

    private fun handleViewClicks() {
        navigateBackButton.setOnClickListener {
            dismiss()
        }

        removeImageButton.setOnClickListener {
            imageContainer.isVisible = false
            selectedImageView.setImageDrawable(null)
        }
    }


    private fun initData() {
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

            var imageLoaded = 0

            while (it.moveToNext() && imageLoaded < 15) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val data = it.getString(dataColumn)

                imageList.add(ImageGallery(imageId = "$id", imageName = name, imageData = data))
                imageLoaded++
            }
        }

        setUpImageAdapter(imageList)
    }

    private fun setUpImageAdapter(imageList: MutableList<ImageGallery>) {
        val imageAdapter = GenericAdapter2(
            itemResLayout = R.layout.item_post_comment_image_gallery,
            itemList = imageList,
            bindItem = { itemView, model, position ->
                val imageView: ImageView = itemView.findViewById(R.id.imageView)
                val viewGallery: LinearLayout = itemView.findViewById(R.id.viewGalleryButton)

                viewGallery.isVisible = position == 0

                val imageFile = File(model.imageData)
                picasso.load(imageFile).into(imageView)

                viewGallery.setOnClickListener {
                    viewFullGallery()
                }

                itemView.setOnClickListener {
                    setSelectedImage(imageFile)
                }
            }
        )

        imageRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = imageAdapter
        }
    }

    private fun viewFullGallery() {
        ImageGalleryDialogFragment { imageName, imagePath ->
            val imageFile = File(imagePath)
            setSelectedImage(imageFile)

        }.show(parentFragmentManager, getString(R.string.gallery))
    }

    private fun setSelectedImage(file: File) {
        picasso.load(file).into(selectedImageView)
        imageContainer.isVisible = true
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostCommentDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}