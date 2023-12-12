package com.digitaldream.winskool.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.winskool.R
import com.digitaldream.winskool.dialog.AdminELearningFilePreviewDialogFragment
import com.digitaldream.winskool.models.AttachmentModel
import com.digitaldream.winskool.utils.StudentFileViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class StudentELearningAssignmentSubmissionAdapter(
    private val itemList: MutableList<AttachmentModel>,
    private val studentFileViewModel: StudentFileViewModel,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<StudentELearningAssignmentSubmissionAdapter.FileViewHolder>() {

    private val filePathList = hashMapOf<Int, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_student_assignment_submission_layout,
            parent, false
        )

        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val itemModel = itemList[position]
        holder.bind(itemModel)
    }

    override fun getItemCount() = itemList.size

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileImageView: ImageView = itemView.findViewById(R.id.fileImageView)
        private val fileNameTxt: TextView = itemView.findViewById(R.id.fileNameTxt)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(attachmentModel: AttachmentModel) {
            fileNameTxt.text = attachmentModel.name
            setCompoundDrawable(fileNameTxt, attachmentModel.type)

            progressBar.isVisible = attachmentModel.isNewFile

            val fileSavedPath = createTempDir(itemView, attachmentModel)
            studentFileViewModel.processFile(attachmentModel, fileSavedPath)

            studentFileViewModel.processedFile.observe(lifecycleOwner) { (file, bitmap) ->
                if (file.absolutePath == fileSavedPath) {
                    fileImageView.setImageBitmap(bitmap)
                    filePathList[adapterPosition] = file.absolutePath
                    fileImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    progressBar.isVisible = false
                    attachmentModel.isNewFile = false
                }
            }

            itemView.setOnClickListener {
                viewFiles(itemView, attachmentModel, adapterPosition)
            }

            deleteAttachment(deleteBtn, adapterPosition)
        }
    }

    private fun setCompoundDrawable(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "pdf" -> R.drawable.ic_pdf24
                "word" -> R.drawable.ic_file_word
                "excel" -> R.drawable.ic_file_excel
                else -> R.drawable.ic_image24
            }.let {
                ContextCompat.getDrawable(textView.context, it)
            },
            null, null, null
        )
    }

    private fun createTempDir(itemView: View, attachmentModel: AttachmentModel): String {
        val tempDir = itemView.context.cacheDir
        val name = attachmentModel.name
        val pathName = "temp_$name"
        return File(tempDir, pathName).absolutePath
    }

    private fun viewFiles(itemView: View, attachmentModel: AttachmentModel, filePosition: Int) {
        try {
            val filePath = filePathList[filePosition]
            val uri = filePath?.let { getFileUri(itemView, it) }

            if (uri != null) {
                when (attachmentModel.type) {
                    "image" -> previewImage(attachmentModel, uri)
                    else -> openFileWithIntent(itemView, uri, attachmentModel.type)
                }
            } else {
                Toast.makeText(itemView.context, "Can't open file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFileUri(itemView: View, fileUri: String): Uri? {
        val file = File(fileUri)
        return if (file.exists()) {
            FileProvider.getUriForFile(
                itemView.context,
                "${itemView.context.packageName}.provider",
                file
            )
        } else {
            null
        }
    }

    private fun openFileWithIntent(itemView: View, uri: Uri, fileType: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val mimeType = when (fileType) {
            "pdf" -> "application/pdf"
            "word" -> "application/msword"
            else -> "application/vnd.ms-excel"
        }

        intent.setDataAndType(uri, mimeType)
        itemView.context.startActivity(intent)
    }

    private fun previewImage(attachmentModel: AttachmentModel, uri: Uri) {
        AdminELearningFilePreviewDialogFragment(
            uri,
            attachmentModel.name
        ).show(fragmentManager, "view file")
    }

    private fun deleteAttachment(deleteButton: ImageButton, position: Int) {
        deleteButton.setOnClickListener {
            itemList.removeAt(position)
            updateFile(it.context)
            notifyItemRemoved(position)
        }
    }


    private fun updateFile(context: Context) {
        val fileArray = JSONArray()

        itemList.forEach { file ->
            JSONObject().apply {
                put("name", file.name)
                put("type", file.type)

                val encodedFile = Base64.encodeToString(file.uri as ByteArray, Base64.DEFAULT)
                put("uri", encodedFile)
            }.let {
                fileArray.put(it)
            }
        }

        context.getSharedPreferences("loginDetail", MODE_PRIVATE)
            .edit().putString("attachment", fileArray.toString()).apply()
    }
}