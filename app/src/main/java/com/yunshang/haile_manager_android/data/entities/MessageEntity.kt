package com.yunshang.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.CommonKeyValueEntity
import com.yunshang.haile_manager_android.data.rule.IMessageContent
import com.yunshang.haile_manager_android.utils.DateTimeUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 10:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class MessageEntity(
    val id: Int,
    val appType: Int,
    val typeId: Int,
    val subtypeId: Int,
    val subtype: String,
    val accountId: Int,
    val logo: String,
    val title: String,
    val content: String,
    val msgKey: String,
    val read: Int,
    val createTime: String,
    val readTime: String
) {
    val contentEntity: IMessageContent?
        get() = if (subtype == "merchant:device:fault") GsonUtils.json2Class(
            content, MessageContentEntity::class.java
        ) else GsonUtils.json2Class(content, MessageSystemContentEntity::class.java)

    val friendTime: String
        get() = DateTimeUtils.getFriendlyTime(DateTimeUtils.formatDateFromString(createTime), false)
}

data class MessageContentEntity(
    val faultMsg: String,
    val faultType: String,
    val goodsId: Int,
    val goodsName: String,
    val introduction: String,
    val shopId: Int,
    val shopName: String,
    val shortDescription: String,
    val subject: String,
    val tags: String,
    val title: String
) : IMessageContent {
    override fun introduction(): String = introduction

    override fun tags(): String = tags

    override fun items(): List<CommonKeyValueEntity> = listOf(
        CommonKeyValueEntity(StringUtils.getString(R.string.shop_name), shopName),
        CommonKeyValueEntity(StringUtils.getString(R.string.device_name), goodsName),
        CommonKeyValueEntity(StringUtils.getString(R.string.break_down_type), faultType),
        CommonKeyValueEntity(StringUtils.getString(R.string.break_down_desc), faultMsg),
    )
}

data class MessageSystemContentEntity(
    val Item: List<MessageSystemContentItemEntity>,
    val introduction: String,
    val shortDescription: String,
    val subject: String,
    val tags: String,
    val title: String,
) : IMessageContent {
    override fun introduction(): String = introduction

    override fun tags(): String = tags

    override fun items(): List<CommonKeyValueEntity> = Item.map {
        CommonKeyValueEntity(it.title, it.msgKey)
    }
}

data class MessageSystemContentItemEntity(
    val msgKey: String,
    val title: String
)