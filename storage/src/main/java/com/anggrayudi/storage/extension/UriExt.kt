@file:JvmName("UriUtils")

package com.anggrayudi.storage.extension

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.storageId
import com.anggrayudi.storage.media.MediaFile
import java.io.*

/**
 * Created on 12/15/20
 * @author Anggrayudi H
 */

/**
 * If given [Uri] with path `/tree/primary:Downloads/MyVideo.mp4`, then return `primary`.
 */
val Uri.storageId: String
    get() {
        val path = path.orEmpty()
        return if (isRawFile) {
            File(path).storageId
        } else when {
            isExternalStorageDocument -> path.substringBefore(':', "").substringAfterLast('/')
            isDownloadsDocument -> DocumentFileCompat.PRIMARY
            else -> ""
        }
    }

val Uri.isTreeDocumentFile: Boolean
    get() = path?.startsWith("/tree/") == true

val Uri.isExternalStorageDocument: Boolean
    get() = authority == DocumentFileCompat.EXTERNAL_STORAGE_AUTHORITY

val Uri.isDownloadsDocument: Boolean
    get() = authority == DocumentFileCompat.DOWNLOADS_FOLDER_AUTHORITY

val Uri.isMediaDocument: Boolean
    get() = authority == DocumentFileCompat.MEDIA_FOLDER_AUTHORITY

val Uri.isRawFile: Boolean
    get() = scheme == ContentResolver.SCHEME_FILE

val Uri.isMediaFile: Boolean
    get() = authority == MediaStore.AUTHORITY

fun Uri.toMediaFile(context: Context) = if (isMediaFile) MediaFile(context, this) else null

fun Uri.toDocumentFile(context: Context) = DocumentFileCompat.fromUri(context, this)

@JvmOverloads
@WorkerThread
fun Uri.openOutputStream(context: Context, append: Boolean = true): OutputStream? {
    return try {
        if (isRawFile) {
            FileOutputStream(File(path ?: return null), append)
        } else {
            context.contentResolver.openOutputStream(this, if (append && isTreeDocumentFile) "wa" else "w")
        }
    } catch (e: IOException) {
        null
    }
}

@WorkerThread
fun Uri.openInputStream(context: Context): InputStream? {
    return try {
        if (isRawFile) {
            // handle file from external storage
            FileInputStream(File(path ?: return null))
        } else {
            context.contentResolver.openInputStream(this)
        }
    } catch (e: IOException) {
        null
    }
}