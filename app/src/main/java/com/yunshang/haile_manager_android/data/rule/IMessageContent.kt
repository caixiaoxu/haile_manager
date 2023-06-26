package com.yunshang.haile_manager_android.data.rule

import com.yunshang.haile_manager_android.data.common.CommonKeyValueEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/26 19:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface IMessageContent {

    fun introduction(): String

    fun tags(): String

    fun items(): List<CommonKeyValueEntity>
}