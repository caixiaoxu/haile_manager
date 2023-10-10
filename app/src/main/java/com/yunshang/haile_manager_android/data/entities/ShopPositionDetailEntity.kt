package com.yunshang.haile_manager_android.data.entities

import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.lsy.framelib.data.constants.Constants
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.BusinessHourEntity
import com.yunshang.haile_manager_android.data.arguments.BusinessHourParams
import com.yunshang.haile_manager_android.data.arguments.ShopParam
import com.yunshang.haile_manager_android.data.extend.hasVal
import com.yunshang.haile_manager_android.data.rule.IMultiTypeEntity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/10 16:02
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
data class ShopPositionDetailEntity(
    val address: String? = null,
    val area: String? = null,
    val code: String? = null,
    val createTime: String? = null,
    val creator: String? = null,
    val id: Int? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val name: String? = null,
    val serviceTelephone: String? = null,
    val sex: Int? = null,
    val shopId: Int? = null,
    val shopName: String? = null,
    var state: Int? = null,
    val workTime: String? = null,
    val workTimeStr: String? = null
) : BaseObservable() {

    @get:Bindable
    var stateVal: Int?
        get() = state
        set(value) {
            state = value
            notifyPropertyChanged(BR.stateVal)
            notifyPropertyChanged(BR.stateNameVal)
        }

    @get:Bindable
    val stateNameVal: String
        get() = StringUtils.getString(if (1 == stateVal) R.string.enable else R.string.disEnable)

    fun hasShopName(): Boolean = shopId.hasVal() && !shopName.isNullOrEmpty()
    fun hasArea(): Boolean = !area.isNullOrEmpty()
    fun hasOrientation(): Boolean = lat.hasVal() && lng.hasVal()
    fun hasAddress(): Boolean = !address.isNullOrEmpty()
    fun hasSex(): Boolean = sex.hasVal()
    val sexNameVal: String
        get() {
            var index = sex
            return if (null == index || index < 0) "" else {
                if (index > 2) index = 2
                StringUtils.getStringArray(R.array.sex_list)[index]
            }
        }

    fun hasWorkTime(): Boolean = !workTime.isNullOrEmpty() || !workTimeStr.isNullOrEmpty()
    fun workTimeArr(): MutableList<BusinessHourEntity>? =
        GsonUtils.json2List(workTimeStr, BusinessHourParams::class.java)?.map {
            BusinessHourEntity().apply {
                formatData(it.weekDays, it.workTime)
            }
        }?.toMutableList() ?: run {
            try {
                GsonUtils.json2List(workTime, String::class.java)?.mapIndexed { index, s ->
                    BusinessHourEntity(listOf(ShopParam.businessDay[index]), s.replace(",", " "))
                }?.filter { item -> item._workTime.isNotEmpty() }?.toMutableList()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } ?: run {
                workTime?.let {
                    mutableListOf(
                        BusinessHourEntity(ShopParam.businessDay, workTime)
                    )
                }
            }
        }

    fun workTimeVal(): String = workTimeArr()?.let {
        it.joinToString("\n") { item ->
            item.hourWeekVal + item.workTime
        }
    } ?: workTime ?: ""

    fun hasCreator(): Boolean = !creator.isNullOrEmpty()
    fun hasCreateTime(): Boolean = !createTime.isNullOrEmpty()
}