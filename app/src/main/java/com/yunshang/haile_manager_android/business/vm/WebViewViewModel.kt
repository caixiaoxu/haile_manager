package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.web.bean.JsScanRequestBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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

    var jsScanRequestBean: JsScanRequestBean? = null
    var callbackId: String? = null

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

    /**
     * 上传头像
     */
    fun uploadImg(path: String, callback: (isSuccess: Boolean, url: String?) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCommonService.updateLoadFile(
                    ApiRepository.createFileUploadBody(path)
                )
            )?.let {
                Timber.i("图片上传成功：$it")
                withContext(Dispatchers.Main) {
                    callback(true, it)
                }
            }
        }, {
            Timber.e("图片上传失败$it")
            withContext(Dispatchers.Main) {
                callback(false, null)
            }
        })
    }
}