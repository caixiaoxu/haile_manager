package com.yunshang.haile_manager_android.web.bean

import com.yunshang.haile_manager_android.data.entities.Organization
import com.yunshang.haile_manager_android.data.entities.UserInfo

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/24 14:50
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
data class JsUserResponseBean(
    val token: String,
    val userId: Int,
    val userInfo: UserInfo,
    val organization: Organization
)