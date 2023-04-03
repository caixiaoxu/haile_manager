package com.lsy.framelib.data.constants

import android.annotation.SuppressLint
import android.content.Context

/**
 * Title : 常量数据
 * Author: Lsy
 * Date: 2023/3/17 11:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
@SuppressLint("StaticFieldLeak")
object Constants {

    // 记录AppL
    lateinit var APP_CONTEXT: Context

    // 超时时间
    const val CONNECTION_TIMEOUT = 15L

    // 失败重试
    const val IS_RETRY_ON_CONNECTION_FAILURE = true

    const val API_VERSION = "1"
    const val API_PLATFORM = "Android"
}