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
class ShopPositionSelectorViewModel: BaseViewModel() {
    private val mShopRepo = ApiRepository.apiClient(ShopService::class.java)

    val canSelectAll:MutableLiveData<Boolean> = MutableLiveData(false)

    // 搜索内容
    val searchContent: MutableLiveData<String> = MutableLiveData()

    val shopPositionList:MutableLiveData<MutableList<ShopAndPositionSelectEntity>?> by lazy { MutableLiveData() }
    // 是否显示列表
    val showList:LiveData<Boolean> = shopPositionList.map {
        !it.isNullOrEmpty()
    }

    fun requestShopPositionList(){
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