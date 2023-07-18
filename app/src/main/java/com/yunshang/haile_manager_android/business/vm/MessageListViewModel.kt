package com.yunshang.haile_manager_android.business.vm

import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.MessageService
import com.yunshang.haile_manager_android.data.entities.MessageEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/26 10:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MessageListViewModel : BaseViewModel() {
    private val mMessageRepo = ApiRepository.apiClient(MessageService::class.java)

    var typeId: Int = -1

    fun requestMessageList(
        page: Int,
        pageSize: Int,
        callBack: (responseList: ResponseList<out MessageEntity>?) -> Unit
    ) {
        if (-1 == typeId) return

        launch({
            ApiRepository.dealApiResult(
                mMessageRepo.messageList(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "pageNum" to page,
                            "pageSize" to pageSize,
                            "appType" to 1,
                            "typeId" to typeId,
                        )
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    callBack.invoke(it)
                }
            }
        }, {
            Timber.d("请求失败或异常$it")
            withContext(Dispatchers.Main) {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                callBack.invoke(null)
            }
        })
    }
}
