package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.HaiXinService
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
class HaiXinRechargeViewModel : BaseViewModel() {
    val mHaiXinRepo = ApiRepository.apiClient(HaiXinService::class.java)

    val userPhone: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val selectShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    val exchangeRate: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val rewardVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val rewardHaiXinVal: MediatorLiveData<String> = MediatorLiveData("").apply {
        addSource(exchangeRate) {
            value = calRewardAmountVal()
        }
        addSource(rewardVal) {
            value = calRewardAmountVal()
        }
    }

    private fun calRewardAmountVal(): String {
        if (null == rewardVal.value || null == exchangeRate.value) {
            return ""
        }

        return try {
            (rewardVal.value!!.toDouble() * exchangeRate.value!!).toInt().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "0"
        }
    }

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(userPhone) {
            value = checkSubmit()
        }
        addSource(selectShop) {
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
        !userPhone.value.isNullOrEmpty() && null != selectShop.value && !rewardVal.value.isNullOrEmpty()
                && try {
            rewardVal.value!!.toDouble() > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }


    fun shopChargeRate() {
        if (-1 == selectShop.value?.id) return
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.requestSchemeDetail(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "shopId" to selectShop.value!!.id
                        )
                    )
                )
            )?.let {
                exchangeRate.postValue(it.exchangeRate)
            }
        })
    }

    fun chargeSubmit(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mHaiXinRepo.rechargeHaiXin(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "account" to userPhone.value!!,
                            "shopId" to selectShop.value!!.id,
                            "presentAmount" to rewardHaiXinVal.value!!
                        )
                    )
                )
            )
            withContext(Dispatchers.Main){
                jump.postValue(1)
            }
        })
    }
}