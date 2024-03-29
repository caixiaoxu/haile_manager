package com.yunshang.haile_manager_android.business.vm

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.OrderService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.OrderDetailEntity
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
class OrderDetailViewModel : BaseViewModel() {
    private val mOrderRepo = ApiRepository.apiClient(OrderService::class.java)

    var orderId = -1
    var orderNo: String? = null

    var isAppoint = false

    val orderDetail: MutableLiveData<OrderDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestOrderDetail() {
        if (-1 == orderId && orderNo.isNullOrEmpty()) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                if (orderNo.isNullOrEmpty())
                    mOrderRepo.requestOrderDetail(orderId)
                else
                    mOrderRepo.requestOrderDetailByNo(orderNo!!)
            )?.let {
                orderDetail.postValue(it)
            }
        })
    }

    /**
     * 复制内容
     */
    fun copyToClipBroad(view: View, txt: String) {
        (view.context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager).run {
            setPrimaryClip(
                ClipData.newPlainText(
                    "",
                    txt
                )
            )
        }

        SToast.showToast(view.context, "已复制到剪切板")
    }

    fun orderOperate(context: Context, type: Int) {
        if (null == orderDetail.value) {
            return
        }
        launch({
            when (type) {
                0 -> orderRefund(context)
                1 -> orderStart(context)
                2 -> orderRestart(context)
            }
        })
    }

    fun cancelOrder(context: Context, orderNo: String, reason: String) {
        launch({
            ApiRepository.dealApiResult(
                mOrderRepo.cancelOrder(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "orderNo" to orderNo,
                            "reason" to reason,
                        )
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(context, StringUtils.getString(R.string.cancel_success))
            }
            LiveDataBus.post(BusEvents.ORDER_LIST_STATUS, orderId)
            requestOrderDetail()
        })
    }

    /**
     * 订单退款
     */
    suspend fun orderRefund(context: Context) {
        ApiRepository.dealApiResult(
            mOrderRepo.requestOrderRefund(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "orderNo" to orderDetail.value!!.orderNo,
                        "refundMethod" to 1,
                    )
                )
            )
        )
        withContext(Dispatchers.Main) {
            SToast.showToast(context, StringUtils.getString(R.string.refund_success))
        }
        LiveDataBus.post(BusEvents.ORDER_LIST_STATUS, orderId)
        requestOrderDetail()
    }

    /**
     * 启动
     */
    suspend fun orderStart(context: Context) {
        ApiRepository.dealApiResult(
            mOrderRepo.requestOrderStart(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "orderNo" to orderDetail.value!!.orderNo,
                    )
                )
            )
        )
        withContext(Dispatchers.Main) {
            SToast.showToast(context, StringUtils.getString(R.string.start_success))
        }
    }

    /**
     * 复位
     */
    suspend fun orderRestart(context: Context) {
        ApiRepository.dealApiResult(
            mOrderRepo.requestOrderRestart(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "orderNo" to orderDetail.value!!.orderNo,
                    )
                )
            )
        )
        withContext(Dispatchers.Main) {
            SToast.showToast(context, StringUtils.getString(R.string.restart_success))
        }
    }
}