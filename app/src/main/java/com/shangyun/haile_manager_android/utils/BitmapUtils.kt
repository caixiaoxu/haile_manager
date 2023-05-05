package com.shangyun.haile_manager_android.utils

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/5 18:25
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object BitmapUtils {

    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    fun createQRCodeBitmap(
        content: String?, width: Int, height: Int,
        character_set: String?, error_correction_level: String?,
        margin: String?, color_black: Int, color_white: Int
    ): Bitmap? {
        // 字符串内容判空
        if (content.isNullOrEmpty()) {
            return null
        }
        // 宽和高>=0
        return if (width < 0 || height < 0) {
            null
        } else try {
            /** 1.设置二维码相关配置  */
            val hints: Hashtable<EncodeHintType, String?> = Hashtable()
            // 字符转码格式设置
            if (!character_set.isNullOrEmpty()) {
                hints[EncodeHintType.CHARACTER_SET] = character_set
            }
            // 容错率设置
            if (!error_correction_level.isNullOrEmpty()) {
                hints[EncodeHintType.ERROR_CORRECTION] = error_correction_level
            }
            // 空白边距设置
            if (!margin.isNullOrEmpty()) {
                hints[EncodeHintType.MARGIN] = margin
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象  */
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = color_black //黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 保存图片到本地
     */
    fun saveBitmap(mContext: Context, name: String, bm: Bitmap): Boolean {
        //指定我们想要存储文件的地址
        val saveFile = File(mContext.externalCacheDir?.absolutePath + "/images/", name)
        //判断指定文件夹的路径是否存在
        saveFile.parentFile?.let { parentFile ->
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
        }

        //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
        try {
            val saveImgOut = FileOutputStream(saveFile)
            // compress - 压缩的意思
            bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut)
            //存储完成后需要清除相关的进程
            saveImgOut.flush()
            saveImgOut.close()
            return true
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return false
    }

}