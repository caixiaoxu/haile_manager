package com.shangyun.haile_manager_android.data.rule

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/30 10:49
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface IIncomeDetailEntity {

    /**
     * 正负
     */
    fun isPlusOrMinus(): Boolean

    /**
     * 总收入
     */
    fun getTotalStr(): String

    /**
     * 标签
     */
    fun getTag(): String

    /**
     * 内容列表
     */
    fun getInfoList(): ArrayList<IncomeDetailInfo>

    /**
     * 是否查看订单
     */
    fun canGoOrderDetail(): Boolean

}

data class IncomeDetailInfo(
    val title: String,
    val value: String,
    val canCopy: Boolean = false
)