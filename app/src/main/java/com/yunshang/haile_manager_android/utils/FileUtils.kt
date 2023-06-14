package com.yunshang.haile_manager_android.utils

import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
}