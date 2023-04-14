package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.business.apiService.ShopService
import com.shangyun.haile_manager_android.data.entities.ShopEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
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
class ShopManagerViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(ShopService::class.java)

    val shopList: MutableLiveData<List<ShopEntity>> by lazy {
        MutableLiveData()
    }

    /**
     * 刷新店铺列表
     */
    fun requestShopList(page: Int, pageSize: Int, result: (listWrapper: ResponseList<ShopEntity>) -> Unit) {
        launch({
            val listWrapper = ApiRepository.dealApiResult(mRepo.shopList(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "page" to page,
                        "pageSize" to pageSize,
                    )
                )
            ))
            listWrapper?.let {
                result.invoke(it)
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
    }
}