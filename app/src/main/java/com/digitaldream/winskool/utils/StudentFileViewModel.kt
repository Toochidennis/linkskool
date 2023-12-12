package com.digitaldream.winskool.utils

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digitaldream.winskool.models.AttachmentModel
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class StudentFileViewModel(application: Application) : AndroidViewModel(application) {
    private val _processedFile = MutableLiveData<Pair<File, Bitmap>>()
    val processedFile: LiveData<Pair<File, Bitmap>> = _processedFile

    fun processFile(attachmentModel: AttachmentModel, targetPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedFile = saveToFile(attachmentModel.uri as ByteArray, targetPath)
            val bitmap = processFileBitmap(attachmentModel, savedFile.absolutePath)

            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    _processedFile.postValue(Pair(savedFile, bitmap))
                } else {
                    Toast.makeText(
                        getApplication(), "${attachmentModel.name} not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveToFile(data: ByteArray, filePath: String): File {
        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        outputStream.write(data)
        outputStream.close()
        return file
    }


    private fun processFileBitmap(attachmentModel: AttachmentModel, filePath: String): Bitmap? {
        return when (attachmentModel.type) {
            "word" -> generateWordThumbnail(attachmentModel, filePath)
            "excel" -> generateExcelThumbnail(attachmentModel, filePath)
            "pdf" -> generatePdfThumbnail(filePath)
            else -> generateImageThumbnail(attachmentModel.uri as ByteArray)
        }
    }

    private fun generateImageThumbnail(byteArray: ByteArray): Bitmap? {
        return try {
            Bitmap.createBitmap(
                BitmapFactory.decodeByteArray(
                    byteArray,
                    0,
                    byteArray.size
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
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