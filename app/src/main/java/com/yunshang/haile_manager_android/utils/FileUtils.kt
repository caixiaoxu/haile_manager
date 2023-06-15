package com.yunshang.haile_manager_android.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.R
import java.io.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/14 10:56
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object FileUtils {
    val downloadDir: File = Constants.APP_CONTEXT.getExternalFilesDir("download")!!

    fun saveDownLoadFile(
        inputStream: InputStream,
        fileName: String,
    ): File? {
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val downFile = File(downloadDir, fileName)
        if (downFile.parentFile?.exists() != true) {
            downFile.mkdirs()
        }
        try {
            val bufferSize = 1024 * 8
            val buffer = ByteArray(bufferSize)
            inputStream.use { input ->
                FileOutputStream(downFile).use { fos ->
                    var length: Int
                    BufferedInputStream(input, bufferSize).use { bis ->
                        while (bis.read(buffer, 0, bufferSize).also { length = it } != -1) {
                            fos.write(buffer, 0, length)
                        }
                        fos.flush()
                    }
                }
            }
            return downFile
        } catch (e: Exception) {
            try {
                if (downFile.exists()) {
                    downFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun saveImageToGallery(context: Context, bmp: Bitmap) {
        val values = ContentValues().apply {
            //设置文件的 MimeType
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            //指定保存的文件名，
            put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            //指定保存的文件目录,如果不设置这个值，则会被默认保存到对应的媒体类型的文件夹下，
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            } else {
                put(
                    MediaStore.MediaColumns.DATA,
                    "${Environment.getExternalStorageDirectory().path}${File.separator}${Environment.DIRECTORY_DCIM}${File.separator}${System.currentTimeMillis()}.png"
                )
            }

        }
        //插入文件数据库并获取到文件的Uri
        val insertUri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        insertUri?.let {
            //通过outputStream将图片文件内容写入Url
            context.contentResolver.openOutputStream(it).use { outputStream ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
        SToast.showToast(context, R.string.success_album_hint)
    }
}