package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.rule.ICommonBottomItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/11 10:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RealNameAuthBindingViewModel : BaseViewModel() {
    private val mCommonService = ApiRepository.apiClient(CommonService::class.java)

    var authCode: String? = null

    val authInfo: MutableLiveData<RealNameAuthDetailEntity> by lazy {
        MutableLiveData()
    }

    // 上传图片缓存
    private val imageCache: MutableMap<String, String> = mutableMapOf()

    /**
     * 上传证件照
     */
    fun uploadIdPhoto(path: String, callback: (isSuccess: Boolean, url: String?) -> Unit) {
        if (!imageCache[path].isNullOrEmpty()) {
            callback(true, imageCache[path])
        } else {
            launch({
                ApiRepository.dealApiResult(
                    mCommonService.updateLoadFile(
                        ApiRepository.createFileUploadBody(path)
                    )
                )?.let {
                    Timber.i("图片上传成功：$it")
                    imageCache[path] = it
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
}