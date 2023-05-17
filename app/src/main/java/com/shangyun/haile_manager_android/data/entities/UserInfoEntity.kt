package com.shangyun.haile_manager_android.data.entities

/**
 * Title : 用户信息
 * Author: Lsy
 * Date: 2023/4/6 11:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
 data class UserInfoEntity(
    val organization: Organization,
    val userInfo: UserInfo
)
data class UserInfo(
    val headImage: String,
    val name: String,
    val phone: String,
    val `property`: Int,
    val tagName: String
)

data class Organization(
    val name: String
)

