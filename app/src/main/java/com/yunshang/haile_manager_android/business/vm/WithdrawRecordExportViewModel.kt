package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

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
class WithdrawRecordExportViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)

    val startTime: MutableLiveData<Date> by lazy { MutableLiveData() }

    val startTimeVal: LiveData<String> = startTime.map {
        DateTimeUtils.formatDateTime(it, "yyyy-MM-dd")
    }

    val endTime: MutableLiveData<Date> by lazy {
        MutableLiveData()
    }

    val endTimeVal: LiveData<String> = endTime.map {
        DateTimeUtils.formatDateTime(it, "yyyy-MM-dd")
    }

    val email: MutableLiveData<String> by lazy { MutableLiveData() }


    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(startTime) {
            value = checkSubmit()
        }
        addSource(endTime) {
            value = checkSubmit()
        }
        addSource(email) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        null != startTime.value && null != endTime.value && !email.value.isNullOrEmpty()
                && email.value!!.length >= 5 && email.value!!.length <= 50 && email.value!!.contains(
            "@"
        )

    fun export(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mCapitalRepo.exportWithdraw(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "code" to "withdrawRecord",
                            "paramJson" to GsonUtils.any2Json(
                                hashMapOf(
                                    "startTime" to DateTimeUtils.formatDateTimeStartParam(
                                        startTime.value
                                    ),
                                    "endTime" to DateTimeUtils.formatDateTimeStartParam(
                                        endTime.value
                                    ),
                                )
                            ),
                            "sendType" to 1,
                            "sendAddress" to email.value
                        )
                    )
                )
            )

            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, "导出成功")
            }
            SPRepository.withdrawExportEmail = email.value
            jump.postValue(1)
        })
    }
}