package com.lsy.framelib.network.response

/**
 * Title : 请求
 * Author: Lsy
 * Date: 2023/3/20 14:57
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class HttpResult(
    val status: ApiStatus,   // 状态
    val exception: Exception? = null // 异常
)