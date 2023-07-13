package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import com.lsy.framelib.utils.AppPackageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 17:02
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MainViewModel : BaseViewModel() {
    private val mCommonRepo = ApiRepository.apiClient(CommonService::class.java)

    //选择的id
    val checkId: MutableLiveData<Int> = MutableLiveData(R.id.rb_main_tab_home)

    // 是否选择首页
    val isShowHomeRB: LiveData<Int> =
        checkId.map { if (it == R.id.rb_main_tab_home) View.INVISIBLE else View.VISIBLE }
    val isShowHomeIcon: LiveData<Int> =
        checkId.map { if (it == R.id.rb_main_tab_home) View.VISIBLE else View.GONE }

    fun checkVersion(context: Context, callBack: (appVersion: AppVersionEntity) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCommonRepo.appVersion(
                    AppPackageUtils.getVersionName(
                        context
                    )
                )
            )?.let {
                withContext(Dispatchers.Main) {
                    callBack(it)
                }
            }
        })
    }

    fun downLoadApk(
        appVersion: AppVersionEntity,
        downloadListener: OnDownloadProgressListener,
    ) {
        if (appVersion.updateUrl.isEmpty()) {
            return
        }

        launch({
            ApiRepository.downloadFile(
                appVersion.updateUrl, "${appVersion.name}${appVersion.versionName}.apk",
                downloadListener
            )
        }, {
            withContext(Dispatchers.Main) {
                downloadListener.onFailure(it)
            }
        }, showLoading = false)
    }
}