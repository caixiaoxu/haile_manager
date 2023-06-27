package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.entities.HaixinSchemeConfigEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/14 19:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HaiXinSchemeConfigsViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    /**
     * 方案列表
     */
    fun requestSchemeList(
        page: Int,
        pageSize: Int,
        result: ((responseList: ResponseList<out HaixinSchemeConfigEntity>?) -> Unit)? = null,
    ) {
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestSchemeList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize
                        )
                    )
                )
            )?.let {
                withContext(Dispatchers.Main){
                    result?.invoke(it)
                }
            }
        })
    }
}