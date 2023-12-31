package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DeviceCategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceStateCountPercentEntity
import com.yunshang.haile_manager_android.data.entities.DeviceStateCountsEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    fun requestShopCategory() {
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
                selectCategory.postValue(it.firstOrNull())
            }
        })
    }

    fun requestGoodsCountPercents(callBack: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.requestGoodsCounts(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopIdList" to selectDepartments.value?.map { it.id },
                            "categoryIdList" to selectCategory.value?.categoryId?.let { categoryId ->
                                listOf(categoryId)
                            }
                        )
                    )
                )
            )?.let {
                deviceStateCounts.postValue(it)
            }

            ApiRepository.dealApiResult(
                mDeviceRepo.requestGoodsCountPercents(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopIdList" to selectDepartments.value?.map { it.id }
                        )
                    )
                )
            )?.let {
                deviceStateCountPercents.postValue(it)
            }
        }, complete = {
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }
}