package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.ShopService
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/11 10:40
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopPositionSelectorViewModel : BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    // 是否显示点位
    var showPosition: Boolean = true

    // 是否需要选择门店
    var needSelectShop: Boolean = true

    // 是否可全选
    var canSelectAll: Boolean = true

    // 搜索内容
    val searchContent: MutableLiveData<String> = MutableLiveData()

    // 是否全选
    val isAll: MutableLiveData<Boolean> = MutableLiveData(false)

    val shopPositionList: MutableLiveData<MutableList<ShopAndPositionSelectEntity>?> by lazy { MutableLiveData() }

    // 是否显示列表
    val showList: LiveData<Boolean> = shopPositionList.map {
        !it.isNullOrEmpty()
    }

    /**
     * 检测是否全选或全不选
     */
    fun checkIsAll() {
        shopPositionList.value?.let {
            if (it.all { item -> 2 == item.selectType }) {
                isAll.value = true
            } else if (it.any { item -> 2 != item.selectType }) {
                isAll.value = false
            }
        }
    }

    fun requestShopPositionList() {
        launch({
            val result = ApiRepository.dealApiResult(
                mShopRepo.requestShopSelectListV2(
                    ApiRepository.createRequestBody(hashMapOf())
                )
            )
            shopPositionList.postValue(result)
        })
    }
}