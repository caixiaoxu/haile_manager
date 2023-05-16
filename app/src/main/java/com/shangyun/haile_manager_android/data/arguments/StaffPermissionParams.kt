package com.shangyun.haile_manager_android.data.arguments

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/16 17:06
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class StaffPermissionParams(
    val parent: UserPermissionEntity,
    val child: List<UserPermissionEntity>?,
) : BaseObservable() {

    val childNum: Int = child?.size ?: 0

    @get:Bindable
    val selectNum: Int
        get() = child?.count { it.isCheck } ?: 0

}
