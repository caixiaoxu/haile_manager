package com.yunshang.haile_manager_android.business.vm

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.entities.SkuFuncConfigurationParam
import com.yunshang.haile_manager_android.data.model.ApiRepository


/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceDrinkingFunctionConfigurationViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // goodsId
    var goodsId: Int = -1

    // spuid
    var spuId: Int = -1

    // 设备类型
    var categoryCode: String? = null

    // 旧的方法配置
    var oldConfigurationList: List<SkuFuncConfigurationParam>? = null

    // 饮水配置
    val drinkAttrConfigure: MutableLiveData<DrinkAttrConfigure> by lazy {
        MutableLiveData()
    }

    // 返回参数
    val resultData: MutableLiveData<List<SkuFuncConfigurationParam>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == spuId) {
            return
        }

        launch({
            val list = ApiRepository.dealApiResult(mDeviceRepo.sku(spuId))
            list?.let {
                it.forEach { sku ->
                    // 如果有旧数据，合并旧数据，如果没有，配置默认数据
                    if (!oldConfigurationList.isNullOrEmpty()) {

                    } else {
                        if (sku.extAttr.isNotEmpty()) {
                        }
                    }
                }
            }
        })
    }

    fun save() {
    }

    data class DrinkAttrConfigure(
        var _priceCalculateMode: Int,//1 按时间，2按流量
        var _overTime: String,//过流时间
        var _pauseTime: String,//暂停时间
        var _singlePulseQuantity: String,//单脉冲流量
        var _normalPrice: String,//常温价格
        var _hotPrice: String,//热水价格
        var normalSoldState: Int,//常温开关
        var hotSoldState: Int,//热水开关
    ) : BaseObservable() {
        val priceCalculateModelVal: String
            get() = StringUtils.getString(if (1 == _priceCalculateMode) R.string.for_time else R.string.for_quantity)

        @get:Bindable
        var priceCalculateMode: Int = _priceCalculateMode
            set(value) {
                _priceCalculateMode = value
                field = value
                notifyPropertyChanged(BR.priceCalculateMode)
            }

        @get:Bindable
        var overTime: String = _overTime
            set(value) {
                _overTime = value
                field = value
                notifyPropertyChanged(BR.overTime)
            }

        @get:Bindable
        var pauseTime: String = _pauseTime
            set(value) {
                _pauseTime = value
                field = value
                notifyPropertyChanged(BR.pauseTime)
            }

        @get:Bindable
        var singlePulseQuantity: String = _singlePulseQuantity
            set(value) {
                _singlePulseQuantity = value
                field = value
                notifyPropertyChanged(BR.singlePulseQuantity)
            }

        @get:Bindable
        var normalPrice: String = _normalPrice
            set(value) {
                _normalPrice = value
                field = value
                notifyPropertyChanged(BR.normalPrice)
            }

        @get:Bindable
        var hotPrice: String = _hotPrice
            set(value) {
                _hotPrice = value
                field = value
                notifyPropertyChanged(BR.hotPrice)
            }

        fun setNormalSoldState(checked:Boolean){
            normalSoldState = if (checked) 1 else 2
        }

        fun setHotSoldState(checked:Boolean){
            hotSoldState = if (checked) 1 else 2
        }
    }
}