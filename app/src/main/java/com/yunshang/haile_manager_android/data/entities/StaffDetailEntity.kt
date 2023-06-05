package com.yunshang.haile_manager_android.data.entities

import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity
import com.yunshang.haile_manager_android.ui.view.MultiTypeTextView

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/17 11:06
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class StaffDetailEntity(
    val id: Int,
    val menuList: List<Menu>,
    val `property`: Int,
    val realName: String,
    val shopList: List<Shop>,
    @SerializedName("tagName")
    val _tagName: String,
    val headImage: String,
    val account: String,
) : IMultiTypeEntity {
    val tagName: String
        get() = if (_tagName.isNullOrEmpty()) StringUtils.getString(R.string.admin) else _tagName

    override fun getMultiType(): Int = if (_tagName.isNullOrEmpty()) 0 else 1

    override fun getMultiTypeBgRes(): IntArray = intArrayOf(
        R.drawable.shape_s26f0a258_r4,
        R.drawable.shape_sf7f7f7_r4,
    )

    override fun getMultiTypeTxtColors(): IntArray = intArrayOf(
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

data class Menu(
    val icon: String,
    val id: Int,
    val isAllocate: Int,
    val kind: String,
    val name: String,
    val orderNum: Int,
    val organizationType: Int,
    val parentId: Int,
    val perms: String,
    val shareType: Int,
    val type: Int,
    val url: String,
    val childList: List<Menu>
)

data class Shop(
    val id: Int,
    val name: String
)
