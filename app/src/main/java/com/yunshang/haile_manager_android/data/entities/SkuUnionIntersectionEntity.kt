package com.yunshang.haile_manager_android.data.entities

import com.yunshang.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/25 16:02
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SkuUnionIntersectionEntity(
    val id: Int,
    var name: String,
    var extAttr: String
): IMultiSelectBottomItemEntity{

    override var isCheck: Boolean = false

    override fun getTitle(): String = name

    // 烘干机时间列表
    var extAttrValue: List<ExtAttrBean>? = null
}