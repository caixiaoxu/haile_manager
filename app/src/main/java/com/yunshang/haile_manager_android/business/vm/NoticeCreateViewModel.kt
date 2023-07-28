package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
import com.yunshang.haile_manager_android.data.arguments.StaffParam
import com.yunshang.haile_manager_android.data.entities.NoticeDetailEntity
import com.yunshang.haile_manager_android.data.entities.NoticeTemplateEntity
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
class NoticeCreateViewModel : BaseViewModel() {

    private val mNoticeRepo = ApiRepository.apiClient(NoticeService::class.java)

    var noticeId: Int = -1

    val showTime: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val createTime: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    var templateid: Int = -1

    val templatename: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
    val notice: MutableLiveData<NoticeDetailEntity> by lazy {
        MutableLiveData()
    }
    val takeChargeShop: MutableLiveData<List<SearchSelectParam>> by lazy {
        MutableLiveData()
    }

    val templateList: MutableLiveData<List<NoticeTemplateEntity>> by lazy {
        MutableLiveData()
    }

    fun requestNoticeList() {
        launch({
            ApiRepository.dealApiResult(
                mNoticeRepo.requestNoticeTemplateList(
                    ApiRepository.createRequestBody(
                        hashMapOf("id" to "")
                    )
                )
            )?.let { list ->
                templateList.postValue(list)
            }
        })
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
        })
    }

    fun submit(view: View) {
        val params = hashMapOf<String, Any>(
            "id" to noticeId,
            "templateId" to templateid,
            "startTime" to createTime.value!!.split("~\n")[0] + ":00",
            "endTime" to createTime.value!!.split("~\n")[1] + ":00",
            "templateStartTime" to showTime.value!!.split("~\n")[0] + ":00",
            "templateEndTime" to showTime.value!!.split("~\n")[1] + ":00",
        )
        takeChargeShop.value?.let { list ->
            params["shopIds"] = list.map { shop -> shop.id }
        }
        launch({
            if (noticeId == -1) {
                //增加
                ApiRepository.dealApiResult(
                    mNoticeRepo.requestAddNotice(
                        ApiRepository.createRequestBody(params)
                    )
                )
            } else {
                //修改
                ApiRepository.dealApiResult(
                    mNoticeRepo.requestEditNotice(
                        ApiRepository.createRequestBody(params)
                    )
                )
            }
            withContext(Dispatchers.Main) {
                SToast.showToast(
                    view.context,
                    if (noticeId == -1) R.string.add_success else R.string.update_success
                )
            }
            LiveDataBus.post(BusEvents.NOTICE_LIST_STATUS, true)
            LiveDataBus.post(BusEvents.NOTICE_DETAIL_CHANGE, true)
            jump.postValue(0)
        })
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(templatename) {
            value = checkSubmit()
        }
        addSource(createTime) {
            value = checkSubmit()
        }
        addSource(showTime) {
            value = checkSubmit()
        }
        addSource(takeChargeShop) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !templatename.value.isNullOrEmpty() && !createTime.value.isNullOrEmpty() && null != showTime.value
                && null != takeChargeShop.value


}