package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/22 10:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SubAccountDetailEntity(
    val account: String,
    val id: Int,
    val name: String,
    val shops: List<SubAccountShop>
)

data class SubAccountShop(
    val categories: List<Category>,
    val id: Int,
    val name: String,
    val shopBusiness: List<ShopBusiness>
) {
    val itemSubAccountShopTitle: String
        get() = StringUtils.getString(
            R.string.item_sub_account_shop_title,
            name,
            shopBusiness.joinToString("，") { it.name })
}

data class Category(
    val effectiveDate: String,
    val id: Int,
    val name: String,
    val ratio: Int,
    val settingId: Int
)

data class ShopBusiness(
    val effectiveDate: String,
    val id: Int,
    val name: String,
    val ratio: Int,
    val settingId: Int
)