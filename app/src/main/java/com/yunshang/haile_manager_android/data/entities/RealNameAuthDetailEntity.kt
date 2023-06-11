package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/11 10:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class RealNameAuthDetailEntity(
    val companyLicense: String,
    val companyName: String,
    val companyUsci: String,
    val id: Int,
    val idCardExpirationDate: String,
    val idCardExpirationType: Int,
    val idCardFont: String,
    val idCardName: String,
    val idCardNo: String,
    val idCardReverse: String,
    val reason: String,
    val status: Int,
    val verifyType: Int
) {
    fun verifyTypeName(): String = StringUtils.getStringArray(R.array.verifyType_arr)[verifyType]
}