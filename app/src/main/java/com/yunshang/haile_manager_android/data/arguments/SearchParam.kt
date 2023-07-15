package com.yunshang.haile_manager_android.data.arguments

import com.yunshang.haile_manager_android.data.common.SearchType

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/15 10:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SearchParam(
    @SearchType.ISearchType val type: Int,//0-设备 1-门店 2-订单
    val keyword: String //关键字
)
