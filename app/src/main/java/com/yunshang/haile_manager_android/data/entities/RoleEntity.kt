package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.model.SPRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/2 16:23
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class RoleEntity(
    val account: String,
    val headImage: String,
    val id: Int,
    val `property`: Int,
    val realName: String,
    val tagName: String,
    val organizationId: Int,
    val organizationName: String,
)  {
    /**
     * 是否是当前组织
     */
    fun isCurOrganization():Boolean = id == SPRepository.loginInfo?.userId
}
