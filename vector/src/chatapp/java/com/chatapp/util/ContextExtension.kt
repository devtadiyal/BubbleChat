package com.chatapp.util

import android.content.Context
import android.net.Uri
import com.chatapp.util.FilePickerUtils.getPathFromRemoteUri

fun Context.getFilePath(uri: Uri):String{
    return FilePickerUtils.getPath(this,uri)?:getFilePathFromRemoteUri(uri)
}
fun Context.getFilePathFromRemoteUri(uri: Uri): String {
    return FilePickerUtils.getPathFromRemoteUri(this, uri) ?: ""
}
