package com.yunshang.haile_manager_android.data.entities

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/14 16:31
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ExportHistoryEntity(
    val createTime: String? = null,
    val fileName: String? = null,
    val id: Int? = null,
    val remark: String? = null,
    val sendAddress: String? = null,
    val sendType: Int? = null,
    val status: Int? = null,
    val url: String? = null
) {

    val statusVal: String
        get() = when (status) {
            0 -> "导出中"
            1 -> "成功"
            else -> "失败"
        }


}