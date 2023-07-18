package com.yunshang.haile_manager_android.web.bean

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
data class JsRequestBean<T>(
    val appId: String,
    val data: T
)