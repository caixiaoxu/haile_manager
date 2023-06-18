package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel

class HaiXinRechargeRecycleViewModel : BaseViewModel() {
    val reachVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val reachAmountVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val rewardVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val rewardAmountVal: MutableLiveData<String> by lazy {
        MutableLiveData()
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
        !reachVal.value.isNullOrEmpty() || !rewardVal.value.isNullOrEmpty()
}