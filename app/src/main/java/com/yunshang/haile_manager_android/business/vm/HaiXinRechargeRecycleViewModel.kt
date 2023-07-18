package com.yunshang.haile_manager_android.business.vm

import android.text.Editable
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
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HaiXinRechargeRecycleViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)
    var userId: Int = -1
    var shopId: Int = -1

    val reach: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }
    val reachHint: LiveData<String> = reach.map {
        StringUtils.getString(R.string.maximum_recycle) + it
    }
    val reward: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }
    val rewardHint: LiveData<String> = reward.map {
        StringUtils.getString(R.string.maximum_recycle) + it
    }

    val exchangeRate: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val account: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val shop: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 充值海星值
    val reachVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val reachAmountVal: MediatorLiveData<String> = MediatorLiveData("").apply {
        addSource(reach) {
            value = calReachAmountVal()
        }
        addSource(exchangeRate) {
            value = calReachAmountVal()
        }
        addSource(reachVal) {
            value = calReachAmountVal()
        }
    }

    private fun calReachAmountVal(): String {
        if ((null == reach.value && null == reachVal.value) || null == exchangeRate.value) {
            return ""
        }

        return try {
            String.format(
                "%.2f",
                (if (null == reachVal.value) reach.value!!.toInt() else reachVal.value!!.toInt()) * 1.0 / exchangeRate.value!!
            )
        } catch (e: Exception) {
            e.printStackTrace()
            "0"
        }
    }

    // 赠送海星值
    val rewardVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val rewardAmountVal: MediatorLiveData<String> = MediatorLiveData("").apply {
        addSource(reward) {
            value = calRewardAmountVal()
        }
        addSource(exchangeRate) {
            value = calRewardAmountVal()
        }
        addSource(rewardVal) {
            value = calRewardAmountVal()
        }
    }

    private fun calRewardAmountVal(): String {
        if (null == exchangeRate.value || (null == reward.value && null == rewardVal.value)) {
            return ""
        }

        return try {
            String.format(
                "%.2f",
                (if (null == rewardVal.value) reward.value!!.toInt() else rewardVal.value!!.toInt()) * 1.0 / exchangeRate.value!!
            )
        } catch (e: Exception) {
            e.printStackTrace()
            "0"
        }
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(reachVal) {
            value = checkSubmit()
        }
        addSource(rewardVal) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        (!reachVal.value.isNullOrEmpty() && reachVal.value!!.toInt() > 0)
                || (!rewardVal.value.isNullOrEmpty() && rewardVal.value!!.toInt() > 0)

    fun requestData() {
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestSchemeDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to shopId
                        )
                    )
                )
            )?.let {
                exchangeRate.postValue(it.exchangeRate)
            }
        })
    }

    fun reachTextChange(s: Editable) {
        reach.value?.let {
            try {
                if (s.toString().toInt() > it) {
                    reachVal.value = "$it"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun rewardTextChange(s: Editable) {
        reward.value?.let {
            try {
                if (s.toString().toInt() > it) {
                    rewardVal.value = "$it"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun recycleSubmit(v: View) {
        if (-1 == userId || -1 == shopId) return
        launch({
            val params = hashMapOf<String, Any>(
                "userId" to userId,
                "shopId" to shopId,
            )
            params["principalAmount"] = reachVal.value ?: 0
            params["presentAmount"] = rewardVal.value ?: 0

            ApiRepository.dealApiResult(
                mHaiXinRepo.rechargeRecycle(
                    ApiRepository.createRequestBody(
                        params
                    )
                )
            )
            LiveDataBus.post(BusEvents.HAIXIN_USER_LIST_STATUS, true)
            withContext(Dispatchers.Main){
                SToast.showToast(v.context,R.string.recycle_success)
            }
            jump.postValue(0)
        })
    }
}