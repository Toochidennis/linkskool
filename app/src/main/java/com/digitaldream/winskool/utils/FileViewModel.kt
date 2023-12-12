package com.digitaldream.winskool.utils

import android.app.Application
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.digitaldream.winskool.R
import com.digitaldream.winskool.models.AttachmentModel
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileViewModel(application: Application) : AndroidViewModel(application) {
    private val fileDownloadQueue = Volley.newRequestQueue(application)

    private val _fileProcessed = MutableLiveData<Pair<File, Bitmap>>()
    val fileProcessed: LiveData<Pair<File, Bitmap>> = _fileProcessed

    fun downloadAndProcessFile(attachmentModel: AttachmentModel, targetPath: String) {
        val fileUrl =
            "${getApplication<Application>().getString(R.string.base_url)}/${attachmentModel.uri}"

        viewModelScope.launch {
            val downloadedFile = downloadFile(fileUrl, targetPath)

            val bitmap = withContext(Dispatchers.IO) {
                processFile(attachmentModel, downloadedFile.absolutePath)
            }

            if (bitmap != null) {
                _fileProcessed.postValue(Pair(downloadedFile, bitmap))
            }

        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun downloadFile(fileURL: String, targetPath: String): File {
        return suspendCancellableCoroutine { continuation ->
            try {
                val fileDownloadRequest = FileDownloadRequest(
                    Request.Method.GET,
                    fileURL,
                    targetPath,
                    { downloadFile ->
                        continuation.resume(downloadFile) {
                            downloadFile.delete()
                        }
                    }
                ) { error ->
                    error.printStackTrace()
                }

                fileDownloadQueue.add(fileDownloadRequest)

                continuation.invokeOnCancellation {
                    fileDownloadRequest.cancel()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun processFile(attachmentModel: AttachmentModel, filePath: String): Bitmap? {
        return when (attachmentModel.type) {
            "video" -> generateVideoThumbnail(filePath)
            "word" -> generateWordThumbnail(attachmentModel, filePath)
            "excel" -> generateExcelThumbnail(attachmentModel, filePath)
            "pdf" -> generatePdfThumbnail(filePath)
            else -> null
        }
    }

    private fun generateVideoThumbnail(videoFilePath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoFilePath)

            // Return first frame of the video
            retriever.getFrameAtTime(
                1000000, MediaMetadataRetriever.OPTION_CLOSEST
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }

    private fun generatePdfThumbnail(pdfFilePath: String): Bitmap? {
        val pdfFile = File(pdfFilePath)
        return try {
            // Read pdf file
            val pdfRenderer =
                PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))

            // Get first page of the pdf
            val page = pdfRenderer.openPage(0)

            // Create a Bitmap to hold the thumbnail
            val thumbnailBitmap =
                Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            // Render the page to the Bitmap
            page.render(
                thumbnailBitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )

            // Release resources
            page.close()
            pdfRenderer.close()

            thumbnailBitmap

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateWordThumbnail(
        attachmentModel: AttachmentModel,
        wordPath: String
    ): Bitmap? {
        return try {
            val tempDir = getApplication<Application>().cacheDir
            val fileName = "${attachmentModel.type}.pdf"
            val convertedFilePath = File(tempDir, fileName).absolutePath

            val inputStream = FileInputStream(wordPath)
            val docx = XWPFDocument(inputStream)
            val extractor = XWPFWordExtractor(docx)
            val content = extractor.text
            inputStream.close()

            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(convertedFilePath))
            document.open()

            // Add content from Word document to PDF
            val paragraph = Paragraph(content)
            document.add(paragraph)

            document.close()
            docx.close()

            generatePdfThumbnail(convertedFilePath)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateExcelThumbnail(
        attachmentModel: AttachmentModel,
        excelPath: String
    ): Bitmap? {
        return try {
            val tempDir = getApplication<Application>().cacheDir
            val fileName = "${attachmentModel.type}.pdf"
            val convertedFilePath = File(tempDir, fileName).absolutePath

            // Read the excel document
            val inputStream = FileInputStream(excelPath)
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)
            inputStream.close()

            val pdfDocument = Document()
            PdfWriter.getInstance(pdfDocument, FileOutputStream(convertedFilePath))
            pdfDocument.open()

            // Add content from Excel sheet to PDF
            val table = PdfPTable(sheet.getRow(0).physicalNumberOfCells)
            for (row in sheet) {
                for (cell in row) {
                    table.addCell(cell.toString())
                }
            }
            pdfDocument.add(table)

            pdfDocument.close()

            generatePdfThumbnail(convertedFilePath)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

}