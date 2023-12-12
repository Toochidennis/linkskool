package com.digitaldream.winskool.dialog

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.digitaldream.winskool.R
import com.squareup.picasso.Picasso
import java.io.File


class AdminELearningFilePreviewDialogFragment(
    private val file: Any?,
    private val fileName: String
) : DialogFragment(R.layout.fragment_admin_e_learning_file_preview) {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView(view)

        loadImage()

    }

    private fun setUpView(view: View) {
        view.apply {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            imageView = findViewById(R.id.imageView)

            toolbar.apply {
                title = fileName
                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {

                    dismiss()
                }
            }
        }
    }

    private fun loadImage() {
        val picasso = Picasso.get()
        when (file) {
            is String -> picasso.load(file).into(imageView)
            is Uri -> picasso.load(file).into(imageView)
            is File -> picasso.load(file).into(imageView)
            is Bitmap -> imageView.setImageBitmap(file)
        }

    }

}