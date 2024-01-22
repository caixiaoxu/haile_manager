package com.yunshang.haile_manager_android.utils.scheme

import android.os.Bundle
import com.yunshang.haile_manager_android.data.arguments.IntentParams

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/13 17:54
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindApproveParser : ISchemeURLParser {

    override fun parsePath(): String =
        "com.yunshang.haile_manager_android.ui.activity.device.DeviceUnbindApproveDetailsActivity"

    override fun parseParams(params: String): Bundle {
        val bundle = Bundle()
        // 解析
        SchemeURLHelper.parseParam(params, "id")?.let {
            try {
                bundle.putAll(IntentParams.CommonParams.pack(it.toInt()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return bundle
    }
}