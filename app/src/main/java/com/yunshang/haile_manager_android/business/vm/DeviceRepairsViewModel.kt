package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.DeviceCategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceRepairsEntity
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/22 11:28
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceRepairsViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    val isBatch: MutableLiveData<Boolean> = MutableLiveData(false)

    // 选择的店铺
    val selectShopList: MutableLiveData<MutableList<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val selectShopVal:LiveData<String> = selectShopList.map {
        when (val count: Int? = it?.size) {
            null, 0 -> ""
            1 -> it.firstOrNull()?.name ?: ""
            else -> "选择${count}家"
        }
    }

    // 选择的店铺点位
    val selectPositionList: MutableLiveData<MutableList<ShopAndPositionSelectEntity>> by lazy {
        MutableLiveData()
    }

    val selectPositionVal:LiveData<String> = selectPositionList.map {
        when (val count: Int? = it?.size) {
            null, 0 -> ""
            1 -> it.firstOrNull()?.name ?: ""
            else -> "选择${count}个"
        }
    }

    // 设备类型
    val categoryList: MutableLiveData<List<DeviceCategoryEntity>> = MutableLiveData()

    // 选择的设备类型
    val selectDeviceCategoryList: MutableLiveData<List<DeviceCategoryEntity>?> by lazy {
        MutableLiveData()
    }

    val selectDeviceCategoryVal:LiveData<String> = selectDeviceCategoryList.map {
        when (val count: Int? = it?.size) {
            null, 0 -> "全部设备"
            1 -> it.firstOrNull()?.categoryName ?: "全部设备"
            else -> "选择${count}个"
        }
    }

    // 选择的状态
    val selectStatus: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val selectBatchNum: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        launch({
            // 类目
            ApiRepository.dealApiResult(
                mDeviceRepo.requestGoodsCategoryList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopIdList" to selectShopList.value?.map { it.id }
                        )
                    )
                )
            )?.let {
                categoryList.postValue(it)
            }
        })
    }

    fun requestDeviceRepairsList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out DeviceRepairsEntity>?) -> Unit
    ) {
        launch({
            val result = ApiRepository.dealApiResult(
                mDeviceRepo.requestDeviceRepairsList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                            "shopIds" to selectShopList.value?.map { item -> item.id },
                            "positionIds" to selectPositionList.value?.flatMap { item ->
                                item.positionList?.mapNotNull { pos -> pos.id } ?: listOf()
                            },
                            "categoryCodes" to selectDeviceCategoryList.value?.map { item -> item.categoryCode },
                            "replyStatus" to if (0 == selectStatus.value?.id) null else selectStatus.value?.id
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                callBack(result)
            }
        }, {
            // 自己定义的错误显示报错提示
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                if (it is CommonCustomException) {
                    it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(msg = "网络开小差~")
                }
                callBack(null)
            }
        })
    }

    fun refreshSelectBatchNum(list: MutableList<DeviceRepairsEntity>) {
        val selectNum = list.count { item -> item.selected }
        selectBatchNum.value =
            if (0 == selectNum) "" else "${StringUtils.getString(R.string.selected)} $selectNum"
        isAll.value = list.all { item -> 10 != item.replyStatus || item.selected }
    }
}