package com.chatapp

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.chatapp.util.getFilePath
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun Uri.getUpadatedFileFromUri(context: Context): File? {
    val tmpFilePath = context.getFilePath(this)
    val fileObject = File(tmpFilePath)
    if (fileObject.exists() && fileObject.canRead()) return fileObject
    val inputStream = context.contentResolver.openInputStream(this) ?: return null
    val salt = System.currentTimeMillis().toString()
    val tempFile = createAppFile(context, salt + fileObject.name, fileObject.extension)
    // STEP 2: copy inputStream into the tempFile
    convertFile(inputStream, tempFile)
    // STEP 3: get file path from tempFile for further upload process.
    return tempFile
}


@Throws(IOException::class)
private fun createAppFile(context: Context, fileName: String?, extension: String?): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    return File(storageDir, "$fileName.$extension")
}

private fun convertFile(inputStream: InputStream, outputFile: File) {
    inputStream.use { input ->
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}