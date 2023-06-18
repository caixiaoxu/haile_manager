package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam

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
    val userPhone: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val selectShop: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    val rewardVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val rewardHaiXinVal: MutableLiveData<String> by lazy {
        MutableLiveData()
    }
}