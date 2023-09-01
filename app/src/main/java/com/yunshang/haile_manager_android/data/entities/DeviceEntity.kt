package com.yunshang.haile_manager_android.data.entities

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity
import com.yunshang.haile_manager_android.data.rule.ISearchSelectEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/23 10:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class DeviceEntity(
    val id: Int,
    val spuId: Int,//设备型号
    val spuName: String,
    val categoryId: Int, // 设备类型
    val categoryCode: String,
    val categoryName: String,
    val type: Int,
    val name: String,//设备名称
    val code: String,//付款码
    val soldState: Int,
    val soldStateOp: Int,
    val inventoryType: Int,
    val chargeUnit: Int,
    val price: Int,
    val extAttr: String,
    val creatorId: Int,//创建人
    val creatorName: String,
    val payType: String,
    val organizationId: Int,
    val shopId: Int,//店铺
    val shopName: String,
    val imei: String,//IMEI
    val sn: String,//设备编号
    val communicationType: Int,//通讯类型(10-串口，20-脉冲)
    val company: String,//公司
    val workStatus: Int,//设备状态(10-空闲；20-工作中；30-故障；40-停用)
    val iotStatus: Int,//网络状态(1-在线；2-离线；4-故障)
    val signals: Int,//信号值
    val income: Double,//总收益
    val qrId: Long, // 设备编号
    val faultFlag: Boolean,//是否故障
    val faultReason: String,//故障原因
    val freeFlag: Boolean,//是否免费
) : ISearchSelectEntity, IMultiTypeEntity {

    fun getDeviceCategoryAndNo() = if (id > 0)
        "${StringUtils.getString(R.string.device_category_no)}：${categoryName} $id"
    else
        "${StringUtils.getString(R.string.device_category)}：${categoryName}"

    /**
     * 获取类型
     */
    override fun getMultiType(): Int = when (categoryCode) {
        DeviceCategory.Washing -> 0
        DeviceCategory.Shoes -> 1
        DeviceCategory.Dryer -> 2
        else -> 3
    }

    override fun getMultiTypeBgRes() = intArrayOf(
        R.drawable.shape_strokef0a258_solid26f0a258_r9,
        R.drawable.shape_stroke30c19a_solid2630c19a_r9,
        R.drawable.shape_strokefc7f6f_solid26fc7f6f_r9,
        R.drawable.shape_stroke999999_solid26999999_r9,
    )

    override fun getMultiTypeTxtColors() = intArrayOf(
        ContextCompat.getColor(
            Constants.APP_CONTEXT,
            R.color.colorPrimary
        ),
        Color.parseColor("#30C19A"),
        Color.parseColor("#FC7F6F"),
        Color.parseColor("#999999")
    )

    fun getStatusBgRes() = intArrayOf(
        R.drawable.shape_stroke30c19a_r9,
        R.drawable.shape_strokefc7f6f_r9,
    )

    fun getStatusTxtColors() = intArrayOf(
        Color.parseColor("#30C19A"),
        Color.parseColor("#FC7F6F"),
    )

    /**
     * 获取状态
     */
    fun getDeviceStatus(): Int = when (workStatus) {
        10, 20 -> 0
        30, 40 -> 1
        else -> 0
    }

    /**
     * 获取状态
     * 10-空闲；20-工作中；30-故障；40-停用
     */
    fun getDeviceStatusValue(): String = when (workStatus) {
        10 -> "空闲"
        20 -> "运行中"
        30 -> "故障"
        40 -> "停用"
        else -> ""
    }

    /**
     * 获取信号状态
     */
    fun getSignalsVal(): Int = if (1 == iotStatus) signals else 0

    /**
     * 获取iot状态
     */
    fun getIotStatusValue(): String = when (iotStatus) {
        1 -> "在线"
        2 -> "离线"
        4 -> "故障"
        else -> ""
    }

    /**
     * 显示信号状态
     */
    fun isIotShow(): Boolean = 0 != iotStatus
    override fun getSearchId(): Int = id

    override fun getTitle(): String = getDeviceCategoryAndNo()

    override fun getContent(): Array<String> = arrayOf(
        name, shopName
    )
}