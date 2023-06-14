package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Title :
 * Author: Lsy
 * Date: 2023/6/13 20:33
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class VersionViewModel : BaseViewModel() {
    val appVersion: MutableLiveData<AppVersionEntity> by lazy {
        MutableLiveData()
    }

    val curVersion: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    fun downLoadApk(callback: OnDownloadProgressListener) {
        if (appVersion.value?.updateUrl.isNullOrEmpty()) {
            return
        }
        launch({
            ApiRepository.downloadFile(
                appVersion.value!!.updateUrl,
                "${appVersion.value!!.name}${appVersion.value!!.versionName}.apk",
                callback
            )
        }, {
            withContext(Dispatchers.Main) {
                callback.onFailure(it)
            }
        }, showLoading = false)
    }
}