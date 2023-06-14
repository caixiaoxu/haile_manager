package com.yunshang.haile_manager_android.utils

import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun saveDownLoadFile(
        inputStream: InputStream,
        contentLength: Long,
        fileName: String,
        callBack: OnDownloadProgressListener? = null
    ) {
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val downFile = File(downloadDir, fileName)
        if (downFile.parentFile?.exists() != true) {
            downFile.mkdirs()
        }
        try {
            val buffer = ByteArray(1024)
            var lastProgress = 0
            inputStream.use { input ->
                FileOutputStream(downFile).use { fos ->
                    var length: Int
                    var sum: Long = 0
                    while (input.read(buffer).also { length = it } != -1) {
                        fos.write(buffer, 0, length)
                        sum += length.toLong()
                        val progress = (sum * 100 / contentLength).toInt()
                        if (progress > lastProgress) {
                            lastProgress = progress
                            withContext(Dispatchers.Main){
                                callBack?.onProgress(progress)
                            }
                        }
                    }
                    fos.flush()
                }
            }
            withContext(Dispatchers.Main){
                callBack?.onSuccess(downFile)
            }
        } catch (e: Exception) {
            try {
                if (downFile.exists()) {
                    downFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main){
                callBack?.onFailure(e)
            }
        }
    }
}