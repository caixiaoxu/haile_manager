package com.yunshang.haile_manager_android.data.arguments

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/19 16:11
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class StaffPermission(
    val menuId: Int,
    val dataPermissionRequest: DataPermissionRequest? = null
)

data class DataPermissionRequest(
    var fundsDistributionType: List<Int>? = null,
    var shopIdList: List<Int>? = null,
)