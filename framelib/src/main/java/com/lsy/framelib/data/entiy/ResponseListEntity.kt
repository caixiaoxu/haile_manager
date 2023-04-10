package com.lsy.framelib.data.entiy

/**
 * Title : 列表型数据
 * Author: Lsy
 * Date: 2023/4/10 15:20
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ResponseListEntity<T>(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val items: List<T>
)
