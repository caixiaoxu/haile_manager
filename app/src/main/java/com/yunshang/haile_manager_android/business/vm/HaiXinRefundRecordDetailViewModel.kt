package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.RefundRecordDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/19 14:51
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class HaiXinRefundRecordDetailViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    var id: Int = -1

    val refundRecordDetail: MutableLiveData<RefundRecordDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestData() {
        if (-1 == id) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestRefundRecordDetail(id)
            )?.let {
                refundRecordDetail.postValue(it)
            }
        })
    }

    fun refuseRefund(remark: String) {
        if (refundRecordDetail.value?.refundNo.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.refuseRefund(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "refundNo" to refundRecordDetail.value!!.refundNo,
                            "remark" to remark,
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.HAIXIN_REFUND_RECORD_LIST_STATUS, true)
            requestData()
        })
    }

    fun agreeRefund(failureCallBack: (msg: String) -> Unit) {
        if (refundRecordDetail.value?.refundNo.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.agreeRefund(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "refundNo" to refundRecordDetail.value!!.refundNo,
                        )
                    )
                )
            )
            LiveDataBus.post(BusEvents.HAIXIN_REFUND_RECORD_LIST_STATUS, true)
            requestData()
        }, {
            it.message?.let {msg->
                if (it is CommonCustomException && 18201 == it.code) {
                    withContext(Dispatchers.Main) {
                        failureCallBack.invoke(msg)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        SToast.showToast(msg = msg)
                    }
                }
            }
        })
    }
}