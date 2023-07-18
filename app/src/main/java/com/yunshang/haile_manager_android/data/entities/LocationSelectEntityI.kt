package com.yunshang.haile_manager_android.data.entities

import com.amap.api.services.core.PoiItem
import com.yunshang.haile_manager_android.data.arguments.PoiResultData
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/19 13:53
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class LocationSelectEntityI(val poi: PoiResultData) : ISearchSelectEntity {
    override fun getSearchId(): Int = -1

    override fun getTitle(): String = poi.title

    override fun getContent(): Array<String> = arrayOf(
        poi.address
    )

//    override fun getValue(): String = StringUtils.friendJuli(poi.distance)
}