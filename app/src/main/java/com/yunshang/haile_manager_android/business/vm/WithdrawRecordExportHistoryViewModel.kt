package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.data.entities.ExportHistoryEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/11/14 15:55
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class WithdrawRecordExportHistoryViewModel : BaseViewModel() {

    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    fun requestExportHistory(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out ExportHistoryEntity>?) -> Unit
    ) {
        launch({
            val result = ApiRepository.dealApiResult(
                mCapitalRepo.requestExportHistory(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "code" to "withdrawRecord",
                            "page" to page,
                            "pageSize" to pageSize
                        )
                    )
                )
            )

            withContext(Dispatchers.Main){
                callBack(result)
            }
        },{
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main){
                // 自己定义的错误显示报错提示
                if (it is CommonCustomException) {
                    it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(msg = "网络开小差~")
                }
                callBack(null)
            }
        })
    }
}