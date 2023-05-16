package com.shangyun.haile_manager_android.data.entities

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.rule.IMultiTypeEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/16 14:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class StaffEntity(
    val account: String,
    val headImage: String,
    val id: Int,
    val `property`: Int,
    val realName: String,
    @SerializedName("tagName")
    val _tagName: String
): IMultiTypeEntity{

    val tagName:String
        get() = if (_tagName.isNullOrEmpty()) StringUtils.getString(R.string.admin) else _tagName


    override fun getMultiType(): Int = if (_tagName.isNullOrEmpty()) 0 else 1

    override fun getMultiTypeBgRes(): IntArray = intArrayOf(
        R.drawable.shape_s26f0a258_r4,
        R.drawable.shape_sf7f7f7_r4,
    )

    override fun getMultiTypeTxtColors(): IntArray =intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.colorPrimary
        ),
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.common_txt_color
        ),
    )

}