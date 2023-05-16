package com.shangyun.haile_manager_android.data.entities

import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.rule.IMultiSelectBottomItemEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/26 10:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class SkuEntity(
    val id: Int,
    val spuId: Int,
    val code: String,
    var name: String,
    var feature: String,
    val items: String,
    var price: Double,
    var soldState: Int,
    var extAttr: String,
    val chargeUnit: String,
    var unit: Int,
    val amount: Int,
    val version: Int,
    val lastEditor: Int,
    val deleteFlag: Int,
    val createTime: String,
    val updateTime: String,
    val specValues: List<SpecValue>,
    var pulse: Int
) {
    var unitValue: String?
        get() = unit.toString()
        set(value) {
            try {
                unit = if (value.isNullOrEmpty()) -1 else value.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    var priceValue: String?
        get() = price.toString()
        set(value) {
            try {
                price = if (value.isNullOrEmpty()) -1.0 else value.toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    var pulseValue: String?
        get() = pulse.toString()
        set(value) {
            try {
                pulse = if (value.isNullOrEmpty()) -1 else value.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    // 烘干机时间列表
    var extAttrValue: List<ExtAttrBean>? = null

    // 烘干机选择的时间值
    val extAttrStr: String
        get() {
            return if (extAttrValue.isNullOrEmpty()) ""
            else extAttrValue!!.filter { it.isCheck }.joinToString("/") {
                if (it.isCheck)
                    StringUtils.getString(
                        R.string.unit_minute_num,
                        it.minutes
                    )
                else ""
            }
        }

    /**
     * 封装成请求的参数
     */
    fun getRequestParams() = SkuFuncConfigurationParam(
        id,
        name,
        price,
        pulse,
        unit,
        extAttrValue?.let { list -> GsonUtils.any2Json(list.filter { it.isCheck }) } ?: "",
        feature,
        soldState
    )

    /**
     * 合并旧数据
     */
    fun mergeOld(old: SkuFuncConfigurationParam?) {
        old?.let {
            name = old.name
            price = old.price
            pulse = old.pulse
            unit = old.unit
            if (old.extAttr.isNotEmpty()) {
                val oldExtAttrValue = GsonUtils.json2List(old.extAttr, ExtAttrBean::class.java)
                oldExtAttrValue?.let {
                    extAttrValue = GsonUtils.json2List(extAttr, ExtAttrBean::class.java)
                    extAttrValue?.forEach { ext ->
                        ext.mergeOld(oldExtAttrValue.find { bean -> bean.minutes == ext.minutes })
                    }
                }
            }
            feature = old.feature
            soldState = old.soldState
        }
    }
}

data class SpecValue(
    val code: String,
    val createTime: String,
    val creatorId: Int,
    val creatorName: String,
    val description: String,
    val ext: Ext,
    val id: Int,
    val image: String,
    val lastEditor: Int,
    val name: String,
    val specId: Int,
    val specName: String,
    val updateTime: String
)

data class Ext(
    val minutes: Int,
    val price: String
)

data class ExtAttrBean(
    val minutes: Int,
    var price: Double,
    var pulse: Int,
) : IMultiSelectBottomItemEntity {

    @Transient
    override var isCheck: Boolean = false

    override fun getTitle(): String = StringUtils.getString(R.string.unit_minute_num, minutes)

    var priceValue: String?
        get() = price.toString()
        set(value) {
            try {
                price = if (value.isNullOrEmpty()) -1.0 else value.toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    var pulseValue: String?
        get() = pulse.toString()
        set(value) {
            try {
                pulse = if (value.isNullOrEmpty()) -1 else value.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    /**
     * 合并旧数据
     */
    fun mergeOld(old: ExtAttrBean?) {
        old?.let {
            isCheck = true
            price = old.price
            pulse = old.pulse
        }
    }
}

data class SkuFuncConfigurationParam(
//    val id: Int = -1,
    val skuId: Int,
    val name: String,
    val price: Double,
    val pulse: Int,
    val unit: Int,
    val extAttr: String,
    val feature: String,
    val soldState: Int,
) {
    /**
     * 根据型号区分配置内容
     */
    fun getConfigurationStr(isDryer: Boolean): String =
        if (isDryer) {
            GsonUtils.json2List(extAttr, ExtAttrBean::class.java)?.joinToString("\n") {
                getConfigStr(it.price, it.minutes, it.pulse)
            } ?: ""
        } else {
            getConfigStr(price, unit, pulse)
        }

    /**
     * 配置内容
     */
    private fun getConfigStr(price: Double, minute: Int, pulse: Int) = if (this.pulse > 0)
        StringUtils.getString(
            R.string.unit_device_configuration_hint2,
            price,
            minute,
            pulse
        )
    else
        StringUtils.getString(R.string.unit_device_configuration_hint1, price, minute)

    fun getSoldStateValue() =
        StringUtils.getString(if (1 == soldState) R.string.in_use else R.string.out_of_service)

}