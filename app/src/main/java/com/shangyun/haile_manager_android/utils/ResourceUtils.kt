package com.shangyun.haile_manager_android.utils

import com.lsy.framelib.data.constants.Constants

/**
 * Title : 资源管理类
 * Author: Lsy
 * Date: 2023/4/6 16:35
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ResourceUtils {

    /**
     * 根据资源id获取String
     */
    fun getStringForResId(resId: Int): String = Constants.APP_CONTEXT.resources.getString(resId)
}