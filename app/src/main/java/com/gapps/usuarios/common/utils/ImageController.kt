package com.gapps.usuarios.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import java.io.File

object ImageController {

    fun selectPhotoFromGallery(activityResultLauncher: ActivityResultLauncher<Intent>){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }

    fun savImage(context: Context, id: Long, uri: Uri) {
        val file = File(context.filesDir, id.toString())

        val bytes = context.contentResolver.openInputStream(uri)?.readBytes()!!

        file.writeBytes(bytes)
    }
}