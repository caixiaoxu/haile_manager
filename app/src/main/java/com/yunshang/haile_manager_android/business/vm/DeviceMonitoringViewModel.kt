package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DeviceCategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceEntity
import com.yunshang.haile_manager_android.data.entities.DeviceStateCountPercentEntity
import com.yunshang.haile_manager_android.data.entities.DeviceStateCountsEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/20 11:27
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceMonitoringViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // 选择的店铺
    val selectDepartments: MutableLiveData<MutableList<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    // 类目列表
    val categoryList: MutableLiveData<MutableList<DeviceCategoryEntity>> by lazy {
        MutableLiveData()
    }

    val selectCategory: MutableLiveData<DeviceCategoryEntity> by lazy {
        MutableLiveData()
    }

    val deviceStateCounts: MutableLiveData<DeviceStateCountsEntity> by lazy {
        MutableLiveData()
    }
    val deviceStateCountPercents: MutableLiveData<MutableList<DeviceStateCountPercentEntity>> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            // 类目
            ApiRepository.dealApiResult(
                mDeviceRepo.requestGoodsCategoryList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopIdList" to selectDepartments.value?.map { it.id }
                        )
                    )
                )
            )?.let {
                it.add(0, DeviceCategoryEntity(categoryName = "全部设备"))
                categoryList.postValue(it)
                requestGoodsCountPercents(it.firstOrNull()?.categoryId)
            }
            ApiRepository.dealApiResult(
                mDeviceRepo.requestGoodsCountPercents()
            )?.let {
                deviceStateCountPercents.postValue(it)
            }
        })
    }

    fun refreshGoodsCountPercents() {
        launch({
            requestGoodsCountPercents(selectCategory.value?.categoryId)
        })
    }

    private suspend fun requestGoodsCountPercents(categoryId: Int?) {
        ApiRepository.dealApiResult(
            mDeviceRepo.requestGoodsCounts(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "shopIdList" to selectDepartments.value?.map { it.id },
                        "categoryIdList" to categoryId?.let { listOf(categoryId) }
                    )
                )
            )
        )?.let {
            deviceStateCounts.postValue(it)
        }
    }
}