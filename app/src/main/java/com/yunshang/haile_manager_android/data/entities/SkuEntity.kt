package com.yunshang.haile_manager_android.data.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity
import com.yunshang.haile_manager_android.data.rule.IMultiSelectBottomItemEntity
import java.text.DecimalFormat

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
    var pulse: Int,
    val functionId: String,
    val functionName: String,
    var dosingConfigs: List<DosingConfigs>,
    val extAttrDto: ExtAttrDto
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

    // 饮水机
    var extAttrDrink: JsonObject? = null

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
        soldState,
        functionId
    )

    /**
     * 封装成饮水请求的参数
     */
    fun getDrinkRequestParams() = SkuFuncConfigurationParam(
        id,
        name,
        price,
        pulse,
        unit,
        extAttrDrink.toString(),
        feature,
        soldState,
        functionId
    )

    /**
     * 封装成投放器请求的参数
     */
    fun getDispenserRequestParams() = SkuFuncConfigurationParam(
        id,
        name,
        price,
        pulse,
        unit,
        dosingConfigs.let { list -> GsonUtils.any2Json(list) },
        feature,
        soldState,
        ""
    )

    /**
     * 合并旧数据
     */
    fun mergeOld(old: SkuFuncConfigurationParam?, dryerOrHair: Boolean) {
        old?.let {
            name = old.name
            price = old.price
            pulse = old.pulse
            unit = old.unit
            if (old.extAttr.isNotEmpty() && dryerOrHair) {
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

    /**
     * 合并旧数据
     */
    fun mergeDrinkOld(old: SkuFuncConfigurationParam?) {
        old?.let {
            name = old.name
            price = old.price
            pulse = old.pulse
            unit = old.unit
            if (old.extAttr.isNotEmpty()) {
                extAttrDrink = GsonUtils.json2JsonObject(old.extAttr) ?: JsonObject()
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

data class ExtAttrDto(
    var items: MutableList<ExtAttrDtoItem>
)

data class ExtAttrDtoItem(
    val id: String,
    val functionType: Int,
    val priceType: Int,
    val priceCalculateMode: Int,
    val sequence: Int,
    var pulse: Int,
    var unitAmount: String,
    val unitCode: Int,
    var unitPrice: Double,
    val description: String,
    val isOn: Boolean,
    var pulseVolumeFactor: String,
    val goodsType: Int,
    val compatibleGoodsCategoryCode: List<String>,
    var isDefault: Boolean = false
) : BaseObservable(), IMultiSelectBottomItemEntity {

    fun mergeExtAttr(same: ExtAttrDtoItem) {
        pulse = same.pulse
        unitAmount = same.unitAmount
        unitPrice = same.unitPrice
        isDefault = same.isDefault
        isCheck = same.isCheck
        pulseVolumeFactor = same.pulseVolumeFactor
    }

    override var isCheck: Boolean = true

    override fun getTitle(): String = if (1 == priceCalculateMode) {
        // 流量
        "${unitAmount}${if (1 == unitCode) "ml" else "L"}"
    } else {
        // 时间
        "${unitAmount}${if (1 == unitCode) "秒" else "分钟"}"
    }

    fun getUnitTile(): String = if (1 == priceCalculateMode) {
        // 流量
        StringUtils.getString(R.string.quantity)
    } else {
        // 时间
        StringUtils.getString(R.string.time)
    }

    fun getUnit(): String = if (1 == priceCalculateMode) {
        // 流量
        "${if (1 == unitCode) "（ml）" else "（L）"}"
    } else {
        // 时间
        "${if (1 == unitCode) "（秒）" else "（分钟）"}"
    }

    @get:Bindable
    var pulseVal: String = ""
        get() = pulse.toString()
        set(value) {
            field = value
            try {
                pulse = value.toInt()
                notifyPropertyChanged(BR.pulseVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @get:Bindable
    var unitAmountVal: String = ""
        get() = if (field.isNullOrEmpty()) unitAmount else field
        set(value) {
            field = value
            try {
                value.toDouble()
                unitAmount = value
                notifyPropertyChanged(BR.unitAmountVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    @get:Bindable
    var unitPriceVal: String = ""
        get() = if (field.isNullOrEmpty()) DecimalFormat("0.######").format(unitPrice) else field
        set(value) {
            field = value
            try {
                unitPrice = value.toDouble()
                notifyPropertyChanged(BR.unitPriceVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExtAttrDtoItem) return false
        if (id == other.id) return true
        return super.equals(other)
    }
}

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

data class ExtAttrDrinkBean(
    val waterTypeId: Int,
    var priceCalculateMode: Int,//1 按时间，2按流量
    var overTime: String,//过流时间
    var pauseTime: String,//暂停时间
    var singlePulseQuantity: String,//单脉冲流量
    val priceCalculateUnit: String//计价单位，写死1
)

data class DrinkAttrConfigure(
    val priceCalculateMode: MutableLiveData<Int>,//1 按流量，2按时间
    var _overTime: Int,//过流时间
    var _pauseTime: Int,//暂停时间
    var _singlePulseQuantity: Double,//单脉冲流量
    var items: List<DrinkAttrConfigureItem>
) : BaseObservable() {

    @get:Bindable
    var overTime: String = _overTime.toString()
        set(value) {
            field = value
            _overTime = try {
                value.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
            notifyPropertyChanged(BR.overTime)
        }

    @get:Bindable
    var pauseTime: String = _pauseTime.toString()
        set(value) {
            field = value
            _pauseTime = try {
                value.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
            notifyPropertyChanged(BR.pauseTime)
        }

    @get:Bindable
    var singlePulseQuantity: String = _singlePulseQuantity.toString()
        set(value) {
            field = value
            _singlePulseQuantity = try {
                value.toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
                0.0
            }
            notifyPropertyChanged(BR.singlePulseQuantity)
        }

    data class DrinkAttrConfigureItem(
        var title: String,//标题
        var price: Double,//常温价格
        var soldState: Int,//常温开关
    ) : BaseObservable() {

        @get:Bindable
        var priceValue: String = price.toString()
            set(value) {
                try {
                    field = value
                    price = if (value.isNullOrEmpty()) 0.0 else value.toDouble()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        fun setSoldState(checked: Boolean) {
            soldState = if (checked) 1 else 2
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
    val functionId: String?,
) {
    /**
     * 根据型号区分配置内容
     */
    fun getConfigurationStr(communicationType: Int, isDryer: Boolean): String =
        if (isDryer) {
            GsonUtils.json2List(extAttr, ExtAttrBean::class.java)?.joinToString("\n") {
                getConfigStr(
                    it.price,
                    it.minutes,
                    if (!DeviceCategory.isPulseDevice(communicationType)) 0 else it.pulse
                )
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

data class SkuFunConfigurationV2Param(
    var skuId: Int,
    var name: String,
    var price: Double,
    var pulse: Int,
    var unit: Int,
    var extAttr: String,
    var feature: String,
    var soldState: Int,
    var extAttrDto: ExtAttrDto,
    val id: Int? = null,
) : BaseObservable(), ICommonBottomItemEntity {

    fun mergeSku(sameParam: SkuFunConfigurationV2Param) {
        name = sameParam.nameVal
        price = sameParam.price
        pulse = sameParam.pulse
        unit = sameParam.unit
        extAttr = sameParam.extAttr
        feature = sameParam.feature
        soldState = sameParam.soldState
        extAttrDto.items.forEach {
            sameParam.extAttrDto.items.find { item -> it.id == item.id }
                ?.let { same ->
                    it.mergeExtAttr(same)
                }
        }
    }

    @get:Bindable
    var soldStateVal: Boolean
        get() = (1 == soldState)
        set(value) {
            soldState = if (value) 1 else 2
            notifyPropertyChanged(BR.soldStateVal)
        }

    @get:Bindable
    var nameVal: String
        get() = name
        set(value) {
            name = value
            notifyPropertyChanged(BR.nameVal)
        }

    @get:Bindable
    var featureVal: String
        get() = feature
        set(value) {
            feature = value
            notifyPropertyChanged(BR.featureVal)
        }

    val selectAttrVal: String
        get() = extAttrDto.items.count { item -> item.isCheck }.let {
            if (it > 0) "已配置${it}项" else "未选择配置项"
        } ?: "未选择配置项"

    val isDefaultAttr: ExtAttrDtoItem?
        get() = extAttrDto.items.find { item -> item.isDefault && item.isCheck }

    @get:Bindable
    var defaultOpen: Boolean
        get() = isDefaultAttr?.isDefault ?: false
        set(value) {
            extAttrDto.items.firstOrNull()?.let { first ->
                first.isDefault = value
            }
            notifyPropertyChanged(BR.defaultOpen)
        }

    @get:Bindable
    var defaultUnitAmount: String
        get() = isDefaultAttr?.getTitle() ?: ""
        set(value) {
            notifyPropertyChanged(BR.defaultUnitAmount)
        }

    override fun getTitle(): String = name
}