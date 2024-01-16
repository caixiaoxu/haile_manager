package com.yunshang.haile_manager_android.data.entities

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.data.constants.Constants
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.utils.StringUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/16 14:51
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceUnbindApproveEntity(
    val id: Int? = null,
    val goodsId: Int? = null,
    val goodsName: String? = null,
    val imei: String? = null,
    val createTime: String? = null,
    val shopId: Int? = null,
    val shopName: String? = null,
    val positionId: Int? = null,
    val positionName: String? = null,
    val status: Int? = null
) : BaseObservable() {
    @Transient
    @get:Bindable
    var selected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    fun statusVal(): String = when (status) {
        1 -> "未处理"
        2 -> "批准"
        3 -> "驳回"
        else -> ""
    }

    fun statusColor(): Int = when (status) {
        1 -> ContextCompat.getColor(Constants.APP_CONTEXT, R.color.colorPrimary)
        3 -> ContextCompat.getColor(Constants.APP_CONTEXT, R.color.color_ff5219)
        else -> ContextCompat.getColor(Constants.APP_CONTEXT, R.color.color_black_85)
    }

    val imeiVal: CharSequence
        get() = (imei?.let {
            StringUtils.formatMultiStyleStr(
                "IMEI：$it", arrayOf(
                    ForegroundColorSpan(Color.parseColor("#F0A258"))
                ), 5, it.length + 5
            )
        } ?: "")

    val shopPositionName: String
        get() = shopName + if (positionName.isNullOrEmpty()) "" else "-$positionName"
}