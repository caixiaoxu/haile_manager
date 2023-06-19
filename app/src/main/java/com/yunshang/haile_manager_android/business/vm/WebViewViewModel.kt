package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.data.model.ApiRepository

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/19 18:26
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class WebViewViewModel : BaseViewModel() {
    private val mCommonService = ApiRepository.apiClient(CommonService::class.java)

    val whiteList: MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData()
    }

    fun requestWhiteList() {
        launch({
            ApiRepository.dealApiResult(mCommonService.requestWhiteList())?.let {
                whiteList.postValue(it)
            }
        })
    }
}