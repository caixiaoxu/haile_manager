package com.shangyun.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.rule.SearchSelectRadioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
class SearchSelectRadioViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)
    private val mStaffRepo = ApiRepository.apiClient(StaffService::class.java)

    companion object {
        const val SearchSelectTypeShop = 0
        const val SearchSelectTypeDeviceModel = 1
        const val SearchSelectTypeTakeChargeShop = 2
    }

    // 搜索类型
    val searchSelectType: MutableLiveData<Int> = MutableLiveData()

    val searchSelectTitle: LiveData<String> = searchSelectType.map {
        when (it) {
            SearchSelectTypeShop -> StringUtils.getString(R.string.department)
            SearchSelectTypeDeviceModel -> StringUtils.getString((R.string.device_model))
            SearchSelectTypeTakeChargeShop -> StringUtils.getString(R.string.take_charge_shop)
            else -> ""
        }
    }

    val searchSelectHint: LiveData<String> = searchSelectType.map {
        when (it) {
            SearchSelectTypeShop, SearchSelectTypeTakeChargeShop -> StringUtils.getString(R.string.shop_search_hint)
            SearchSelectTypeDeviceModel -> StringUtils.getString((R.string.device_model_search_hint))
            else -> ""
        }
    }

    val searchSelectListHint: LiveData<String> = searchSelectType.map {
        when (it) {
            SearchSelectTypeShop, SearchSelectTypeTakeChargeShop -> StringUtils.getString(R.string.shop_info)
            SearchSelectTypeDeviceModel -> StringUtils.getString((R.string.device_model))
            else -> ""
        }
    }

    var staffId: Int = -1

    // 设备类型id
    var categoryId: Int = -1

    val searchKey: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val isAllSelect: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    // 原始数据
    var originSelectList: MutableList<out SearchSelectRadioEntity>? = null

    // 选择数据
    val selectList: MutableLiveData<MutableList<out SearchSelectRadioEntity>> by lazy {
        MutableLiveData()
    }

    /**
     * 请求数据
     */
    fun requestSearch() {
        launch({
            val list: MutableList<out SearchSelectRadioEntity> =
                when (searchSelectType.value) {
                    SearchSelectTypeShop, SearchSelectTypeTakeChargeShop -> ApiRepository.dealApiResult(
                        mShopRepo.shopSelectList(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "name" to (searchKey.value ?: ""),
                                )
                            )
                        )
                    )
                    SearchSelectTypeDeviceModel -> {
                        if (originSelectList.isNullOrEmpty()) {
                            if (-1 != categoryId) {
                                val list =
                                    ApiRepository.dealApiResult(mDeviceRepo.spuList(categoryId))
                                originSelectList = list?.let { it[0].spu }
                                originSelectList
                            } else null
                        } else {
                            searchKey.value?.let { key ->
                                originSelectList!!.filter {
                                    it.getSelectName().contains(key)
                                }.toMutableList()
                            } ?: originSelectList!!
                        }
                    }
                    else -> null
                } ?: mutableListOf()
            selectList.postValue(list)
        })
    }

    fun checkSelectAll() {
        viewModelScope.launch(Dispatchers.IO) {
            selectList.value?.let { list ->
                isAllSelect.postValue(list.all { true == it.getCheck()?.value })
            }
        }
    }

    fun updateStaffShop(context: Context, selected: List<SearchSelectRadioEntity>) {
        if (-1 == staffId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mStaffRepo.updateStaffTakeChargeShop(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "userId" to staffId,
                            "subOrganizationIdList" to selected.map { it.getSelectId() },
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, R.string.update_success)
            }
            LiveDataBus.post(BusEvents.STAFF_DETAILS_STATUS, true)
            jump.postValue(0)
        })
    }
}