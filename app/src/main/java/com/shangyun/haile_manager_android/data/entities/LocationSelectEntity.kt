package com.shangyun.haile_manager_android.data.entities

import com.amap.api.services.core.PoiItem
import com.shangyun.haile_manager_android.data.rule.SearchSelectEntity

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
data class LocationSelectEntity(val poi: PoiItem) : SearchSelectEntity {
    override fun getTitle(): String = poi.title

    override fun getContent(): Array<String> = arrayOf(
        poi.provinceName + poi.cityName + poi.adName + poi.snippet
    )

//    override fun getValue(): String = StringUtils.friendJuli(poi.distance)
}