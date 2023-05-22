package com.shangyun.haile_manager_android.data.arguments

import com.shangyun.haile_manager_android.data.rule.ICommonBottomItemEntity
import com.shangyun.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/19 10:41
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ActiveDayParam(val name: String, val id: Int) : ICommonBottomItemEntity,
    IMultiSelectBottomItemEntity {
    override var isCheck: Boolean = false

    override fun getTitle(): String = name

}