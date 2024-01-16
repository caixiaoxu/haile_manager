package com.yunshang.haile_manager_android.data.entities

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/22 15:03
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceRepairsEntity(
    val lastReportTime: String? = null,
    val createTime: String? = null,
    val creator: Int? = null,
    val description: String? = null,
    val deviceId: Int? = null,
    val deviceName: String? = null,
    val fixSubTypeCode: String? = null,
    val fixSubTypeName: String? = null,
    val goodsCategoryCode: String? = null,
    val goodsCategoryName: String? = null,
    val id: Int? = null,
    val pics: List<String>? = null,
    val pointName: String? = null,
    val replyDTOS: List<ReplyDTOS>? = null,
    val replyStatus: Int? = null,
    val shopName: String? = null,
    val userAccount: String? = null,
    val goodsId: Int? = null,
    val noReplyCount: Int? = null,
    val imei: String? = null
) : BaseObservable() {
    @Transient
    @get:Bindable
    var selected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    val shopPositionName: String
        get() = shopName + if (pointName.isNullOrEmpty()) "" else "-$pointName"

    val imeiVal: CharSequence
        get() = (imei?.let {
            StringUtils.formatMultiStyleStr(
                "IMEI：$it", arrayOf(
                    ForegroundColorSpan(Color.parseColor("#F0A258"))
                ), 5, it.length + 5
            )
        } ?: "")
    val imeiVal1: CharSequence
        get() = (imei?.let {
            StringUtils.formatMultiStyleStr(
                it, arrayOf(
                    ForegroundColorSpan(Color.parseColor("#F0A258"))
                ), 0, it.length
            )
        } ?: "")
}

data class ReplyDTOS(
    val createTime: String? = null,
    val deviceFixLogIds: List<Int>? = null,
    val deviceIds: List<Int>? = null,
    val replyContent: String? = null,
    val replyUserId: Int? = null
)