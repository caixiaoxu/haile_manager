package com.shangyun.haile_manager_android.data.entities

import com.shangyun.haile_manager_android.data.rule.ICommonBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/17 16:16
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopTypeEntity(val type: Int, val name: String) : ICommonBottomItemEntity {
    override fun getTitle(): String = name
}