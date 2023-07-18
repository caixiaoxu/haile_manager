package com.yunshang.haile_manager_android.web.bean

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/24 14:50
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
</desc></version></time></author> */
data class JsScanRequestBean(val type: String,val scanType: List<String>) {
    val typeVal: Int
        get() {
            var result = -1
            if (type == "imei") {
                result = 1
            } else if (type == "pay") {
                result = 2
            } else if (type == "sn") {
                result = 3
            } else if (type == "all") {
                result = 0
            }
            return result
        }

    fun getTypeStr(type: Int): String {
        var typeVal = ""
        when (type) {
            -1 -> typeVal = "none"
            0 -> typeVal = "all"
            1 -> typeVal = "imei"
            2 -> typeVal = "pay"
            3 -> typeVal = "sn"
        }
        return typeVal
    }
}