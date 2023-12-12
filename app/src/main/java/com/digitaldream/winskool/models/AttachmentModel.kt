package com.digitaldream.winskool.models

data class AttachmentModel(
    var name: String,
    var oldName: String,
    val type: String,
    var uri: Any?,
    var isNewFile: Boolean = false
)
