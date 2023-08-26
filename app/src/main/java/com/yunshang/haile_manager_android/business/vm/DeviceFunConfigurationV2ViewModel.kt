package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.data.entities.SpuExtAttrDto
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
class DeviceFunConfigurationV2ViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // spuid
    var spuId: Int = -1

    // 设备类型
    val categoryCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否是洗衣机和烘干机
    val isWashingOrDryer: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isWashing(it) || DeviceCategory.isDryer(it)
    }

    val isDrinkingOrShower: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isDrinkingOrShower(it)
    }

    var spuExtAttrDto: SpuExtAttrDto? = null
    val oldConfigureList: MutableList<SkuFunConfigurationV2Param>? = null

    val priceModelList = listOf(
        SearchSelectParam(1, StringUtils.getString(R.string.price_model_type1)),
        SearchSelectParam(2, StringUtils.getString(R.string.price_model_type2)),
    )

    val calculateModelList = listOf(
        SearchSelectParam(1, StringUtils.getString(R.string.for_quantity)),
        SearchSelectParam(2, StringUtils.getString(R.string.for_time)),
    )

    val priceModel: MutableLiveData<SearchSelectParam> = MutableLiveData(priceModelList[0])
    val calculateModel: MutableLiveData<SearchSelectParam> = MutableLiveData(calculateModelList[0])

    val configureList: MutableLiveData<MutableList<SkuFunConfigurationV2Param>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (spuId <= 0) {
            return
        }

        launch({
            oldConfigureList?.let { list ->
                configureList.postValue(list)
            } ?: run {
                ApiRepository.dealApiResult(mDeviceRepo.sku(spuId))?.let {
                    configureList.postValue(it.map { sku -> sku.toFunConfigureV2Param() }
                        .toMutableList())
                }
            }
        })
    }
}