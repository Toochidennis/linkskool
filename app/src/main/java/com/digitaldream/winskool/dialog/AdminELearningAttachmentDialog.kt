package com.digitaldream.winskool.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.digitaldream.winskool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AdminELearningAttachmentDialog
 *
 * Overview:
 * The AdminELearningAttachmentDialog class is a BottomSheetDialogFragment designed for handling file attachments
 * in an e-learning application. It provides options for uploading various file types, capturing images, and recording videos.
 *
 * Constructors:
 * - AdminELearningAttachmentDialog(from: String, onFileSelected: (type: String, name: String, uri: Any?) -> Unit):
 *   Constructor for creating an instance of the dialog.
 *   Parameters:
 *     - from: A string indicating the source or context for using the dialog.
 *     - onFileSelected: A lambda function that gets called when a file is selected.
 *                      It provides information about the file type, name, and URI.
 *
 * Properties:
 * - cameraLauncher: ActivityResultLauncher<Intent>:
 *   Activity result launcher for camera-related actions.
 * - cameraCode: Int:
 *   A variable to distinguish between capturing images (0) and recording videos (1).
 * - timeStamp: String?:
 *   A timestamp used for generating unique file names.
 *
 * Methods:
 * - onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?:
 *   Overrides the onCreateView method to inflate the dialog's layout.
 * - onViewCreated(view: View, savedInstanceState: Bundle?):
 *   Overrides the onViewCreated method to set up the dialog's behavior and UI components.
 * - takePhoto():
 *   Initiates the process of capturing an image using the device's camera.
 * - recordVideo():
 *   Initiates the process of recording a video using the device's camera.
 * - imageFile(): File:
 *   Generates a File object for storing the captured image.
 * - videoFile(): File:
 *   Generates a File object for storing the recorded video.
 * - getFileNameFromUri(uri: Uri): String:
 *   Retrieves the display name of a file from its content URI.
 *
 * Usage:
 * 1. Create an instance of the AdminELearningAttachmentDialog class.
 * 2. Set up the onFileSelected lambda function to handle the selected file information.
 * 3. Show the dialog using show().
 *
 * Example:
 * ```kotlin
 * val attachmentDialog = AdminELearningAttachmentDialog(from = "multiple choice") { type, name, uri ->
 *     // Handle the selected file information
 *     // Type: File type (image, video, pdf, etc.)
 *     // Name: Display name of the file
 *     // URI: Content URI of the file
 * }
 *
 * attachmentDialog.show(parentFragmentManager, "attachmentDialog")
 * ```
 */


class AdminELearningAttachmentDialog(
    private val from: String = "",
    private val onFileSelected: (type: String, name: String, uri: Any?) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var cameraCode = 0
    private var timeStamp: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_admin_elearning_attachment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { fileUri: Uri? ->
                fileUri?.let { uri ->
                    val mimeType = requireActivity().contentResolver.getType(uri)
                    val fileType = when {
                        mimeType?.contains("image") == true -> FileType.IMAGE
                        mimeType?.contains("video") == true -> FileType.VIDEO
                        mimeType == "application/pdf" -> FileType.PDF
                        mimeType == "application/msword" || mimeType ==
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> FileType.WORD

                        mimeType == "text/csv" -> FileType.CSV
                        mimeType == "application/vnd.ms-excel" || mimeType ==
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> FileType.EXCEL

                        else -> FileType.UNKNOWN
                    }


                    when (fileType) {
                        FileType.IMAGE -> onFileSelected(
                            "image", getFileNameFromUri(uri),
                            uri
                        )

                        FileType.VIDEO -> onFileSelected(
                            "video",
                            getFileNameFromUri(uri),
                            uri
                        )

                        FileType.PDF -> onFileSelected(
                            "pdf",
                            getFileNameFromUri(uri),
                            uri
                        )

                        FileType.CSV -> onFileSelected(
                            "csv",
                            getFileNameFromUri(uri),
                            uri
                        )

                        FileType.EXCEL -> onFileSelected(
                            "excel",
                            getFileNameFromUri(uri),
                            uri
                        )

                        FileType.WORD -> onFileSelected(
                            "word",
                            getFileNameFromUri(uri),
                            uri
                        )

                        else -> onFileSelected(
                            "unknown",
                            getFileNameFromUri(uri),
                            uri
                        )
                    }

                    dismiss()
                }
            }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if (cameraCode == 0) {
                    onFileSelected("image", imageFile().name, imageFile())
                } else {
                    val videoUri = result.data?.data
                    videoUri?.let { uri ->
                        val inputStream: InputStream? = requireActivity()
                            .contentResolver
                            .openInputStream(uri)
                        val outputStream: OutputStream = FileOutputStream(videoFile())

                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        onFileSelected("video", videoFile().name, videoFile())

                        inputStream?.close()
                        outputStream.close()
                    }
                }
            } else {
                Toast.makeText(context, "Error getting file", Toast.LENGTH_SHORT).show()
            }

            dismiss()
        }


        view.apply {
            val insertLink: TextView = findViewById(R.id.insertLink)
            val uploadFile: TextView = findViewById(R.id.uploadFile)
            val takePhotoBtn: TextView = findViewById(R.id.takePhoto)
            val recordVideoBtn: TextView = findViewById(R.id.recordVideo)

            when (from) {
                "multiple choice" -> {
                    insertLink.isVisible = false
                    recordVideoBtn.isVisible = false

                    uploadFile.setOnClickListener {
                        filePickerLauncher.launch("image/*")
                    }
                }

                "student" -> {
                    insertLink.isVisible = false
                    recordVideoBtn.isVisible = false

                    uploadFile.setOnClickListener {
                        filePickerLauncher.launch("*/*")
                    }
                }

                else -> {
                    uploadFile.setOnClickListener {
                        filePickerLauncher.launch("*/*")
                    }
                }
            }


            insertLink.setOnClickListener {
                InsertLinkDialog(requireContext()) { url: String ->
                    onFileSelected("url", url, url)
                }.apply {
                    setCancelable(true)
                    show()
                }.window?.setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                dismiss()
            }

            takePhotoBtn.setOnClickListener {
                takePhoto()
            }

            recordVideoBtn.setOnClickListener {
                recordVideo()
            }

        }


        timeStamp = SimpleDateFormat("EEE, dd MMM HH:mm:ss", Locale.getDefault()).format(Date())
    }


    private fun imageFile(): File {
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "$timeStamp.jpg")
    }

    private fun videoFile(): File {
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File(storageDir, "$timeStamp.mp4")
    }


    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraCode = 0

            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.provider",
                imageFile()
            )

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraLauncher.launch(takePictureIntent)

        } else {
            Toast.makeText(requireContext(), "package name is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun recordVideo() {
        val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (recordVideoIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraCode = 1
            cameraLauncher.launch(recordVideoIntent)
        } else {
            Toast.makeText(requireContext(), "package name is null", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                return it.getString(nameIndex) ?: ""
            }
        }
        return "No name"
    }


    enum class FileType {
        IMAGE, VIDEO,
        PDF, WORD,
        EXCEL, CSV,
        UNKNOWN
    }

}