package com.shangyun.haile_manager_android.business.vm

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.StaffService
import com.shangyun.haile_manager_android.data.entities.StaffEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
class StaffManagerViewModel : BaseViewModel() {
    private val mStaffRepo = ApiRepository.apiClient(StaffService::class.java)
    fun requestStaffList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<StaffEntity>?) -> Unit

    ) {
        launch({
            ApiRepository.dealApiResult(
                mStaffRepo.requestStaffList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "page" to page,
                            "pageSize" to pageSize,
                        )
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    result.invoke(it)
                }
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                result.invoke(null)
            }
        }, null, 1 == page)
    }
}