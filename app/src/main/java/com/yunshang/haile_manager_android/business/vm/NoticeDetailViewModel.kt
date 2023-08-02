package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.NoticeService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.NoticeDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
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
class NoticeDetailViewModel : BaseViewModel() {
    private val mNoticeRepo = ApiRepository.apiClient(NoticeService::class.java)

    var noticeId: Int = -1

    val notice: MutableLiveData<NoticeDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestNoticeDetailData() {
        if (-1 == noticeId) {
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mNoticeRepo.requestNoticeDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to noticeId,
                        )
                    )
                )
            )?.let {
                notice.postValue(it)
            }
            withContext(Dispatchers.Main) {

            }
        })
    }

    fun requestNoticeDelete(view: View) {
        if (-1 == noticeId) {
            return
        }
        launch({
            ApiRepository.dealApiResult(
                mNoticeRepo.requestEndNotice(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "id" to noticeId,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(view.context, R.string.notice_stop_success)
            }
            LiveDataBus.post(BusEvents.NOTICE_LIST_ITEM_DELETE_STATUS, noticeId)
            jump.postValue(0)
        })
    }


}