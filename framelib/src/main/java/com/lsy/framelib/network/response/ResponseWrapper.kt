package com.lsy.framelib.network.response

import java.io.Serializable

/**
 * Title : 接口请求的返回数据格式
 * Author: Lsy
 * Date: 2023/3/17 15:37
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ResponseWrapper<out T>(
    val code: Int = -1,
    val message: String = "",
    val version: String = "",
    val timestamp: Long = 0,
    @Transient val data: T? = null,
    var dataState: ApiStatus? = null,
    var error: Throwable? = null
) : Serializable

data class ResponseList<T>(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val items: MutableList<T>
)