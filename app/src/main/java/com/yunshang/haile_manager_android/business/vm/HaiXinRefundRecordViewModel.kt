package com.yunshang.haile_manager_android.business.vm

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.response.ResponseList
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.OrderListEntity
import com.yunshang.haile_manager_android.data.entities.RefundRecordEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.IndicatorEntity
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class HaiXinRefundRecordViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    val searchKeyword: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 选择的店铺
    val selectDepartment: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    val refundStatus = arrayListOf(
        IndicatorEntity("全部", 0, ""),
        IndicatorEntity("待审核", 0, "0"),
        IndicatorEntity("已退款", 0, "1"),
        IndicatorEntity("退款失败", 0, "2"),
        IndicatorEntity("确认失败", 0, "4"),
        IndicatorEntity("已拒绝", 0, "3"),
    )

    val curRefundStatus: MutableLiveData<String> = MutableLiveData("")

    val mRecordCountStr: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun requestRefundRecordList(
        page: Int,
        pageSize: Int,
        result: (listWrapper: ResponseList<RefundRecordEntity>?) -> Unit
    ) {
        launch({
            val params: HashMap<String, Any> = hashMapOf(
                "page" to page,
                "pageSize" to pageSize,
                "state" to (curRefundStatus.value ?: ""),
            )
            // 店铺
            selectDepartment.value?.let {
                params["shopId"] = it.id
            }
            // 关键字
            if (!searchKeyword.value.isNullOrEmpty()) {
                if (searchKeyword.value!!.length > 11) {
                    params["refundNo"] = searchKeyword.value!!
                } else {
                    params["account"] = searchKeyword.value!!
                }
            }

            ApiRepository.dealApiResult(
                mHaiXinRepo.requestRefundRecordList(ApiRepository.createRequestBody(params))
            )?.let {
                mRecordCountStr.postValue(
                    StringUtils.getString(R.string.order_num_hint, it.total),
                )
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