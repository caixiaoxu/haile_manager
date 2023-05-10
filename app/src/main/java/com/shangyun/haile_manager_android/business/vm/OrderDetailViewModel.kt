package com.shangyun.haile_manager_android.business.vm

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.OrderService
import com.shangyun.haile_manager_android.data.entities.OrderDetailEntity
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
class OrderDetailViewModel : BaseViewModel() {
    private val mOrderRepo = ApiRepository.apiClient(OrderService::class.java)

    var orderId = -1

    val orderDetail: MutableLiveData<OrderDetailEntity> by lazy {
        MutableLiveData()
    }

    fun requestOrderDetail() {
        if (-1 == orderId) {
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mOrderRepo.requestOrderDetail(orderId)
            )?.let {
                orderDetail.postValue(it)
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, { Timber.d("请求结束") })
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
}