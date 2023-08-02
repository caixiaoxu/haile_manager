package com.yunshang.haile_manager_android.data.entities

import android.view.View

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
data class NoticeDetailEntity(
    val id: Int,
    val templateId: Int,
    val state: Int,
    val userId: Int,
    val startTime: String,
    val endTime: String,
    val account: String,
    val createTime: String,
    val templateName: String,
    val templateContent: String,
    val templateStartTime: String,
    val templateEndTime: String,
    val noticeShopDtos: List<NoticeShopDtosBean>,
) {
    fun time(): String = "${startTime}~\n${endTime}"
    fun showtime(): String = "${templateStartTime}~\n${templateEndTime}"
    fun shopnames(): String {
        val shopname = StringBuilder()
        noticeShopDtos.forEach {
            shopname.append("${it.shopName},")
        }
        if (shopname.isNotEmpty()) {
            shopname.deleteCharAt(shopname.length - 1)
        }
        return shopname.toString()
    }
    fun isBottomShow(): Boolean = (state == 0 || state == 1)
    fun notice_state(): String {
        val statename: String
        when (state) {
            0 -> {
                statename = "未开始"
            }

            1 -> {
                statename = "进行中"
            }

            2 -> {
                statename = "已结束"
            }

            else -> {
                statename = ""
            }
        }
        return statename
    }

}

data class NoticeShopDtosBean(
    val id: Int,
    val noticeId: Int,
    val shopId: Int,
    val shopName: String,
)
