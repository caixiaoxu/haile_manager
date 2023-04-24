package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.DeviceService
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.rule.SearchSelectRadioEntity
import timber.log.Timber

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

    companion object {
        const val SearchSelectTypeShop = 0
        const val SearchSelectTypeDeviceModel = 1
    }

    // 搜索类型
    val searchSelectType: MutableLiveData<Int> = MutableLiveData()

    // 设备类型id
    var categoryId: Int = -1

    val searchKey: MutableLiveData<String> by lazy {
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
            val list: MutableList<out SearchSelectRadioEntity>? =
                when (searchSelectType.value) {
                    SearchSelectTypeShop -> ApiRepository.dealApiResult(
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
                }
            selectList.postValue(list)
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        })
    }
}