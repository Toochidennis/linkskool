package com.digitaldream.winskool.utils

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.File
import java.io.FileOutputStream

class FileDownloadRequest(
    method: Int,
    url: String,
    private val filePath: String,
    private val successListener: (File) -> Unit,
    errorListener: (Exception) -> Unit
) : Request<File>(method, url, Response.ErrorListener { error -> errorListener(error) }) {


    override fun parseNetworkResponse(response: NetworkResponse?): Response<File> {
        return try {
            val file = saveToFile(response!!.data)
            Response.success(file, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: VolleyError) {
            Response.error(e)
        }
    }

    override fun deliverResponse(response: File) {
        successListener(response)
    }

    override fun getErrorListener(): Response.ErrorListener? {
        return errorListener
    }

    private fun saveToFile(data: ByteArray): File {
        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        outputStream.write(data)
        outputStream.close()
        return file
    }
}