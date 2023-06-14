package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.utils.AppPackageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class SettingViewModel : BaseViewModel() {
    private val mLoginUserRepo = ApiRepository.apiClient(LoginUserService::class.java)
    private val mCommonRepo = ApiRepository.apiClient(CommonService::class.java)

    val appVersionInfo: MutableLiveData<AppVersionEntity> by lazy {
        MutableLiveData()
    }

    fun checkVersion(context: Context) {
        launch({
            ApiRepository.dealApiResult(mCommonRepo.appVersion(AppPackageUtils.getVersionName(context)))
                ?.let {
                    appVersionInfo.postValue(it)
                }
        })
    }

    fun cancelAccount(callBack: () -> Unit) {
        launch({
            ApiRepository.dealApiResult(mLoginUserRepo.cancelAccount())
            withContext(Dispatchers.Main) {
                callBack()
            }
        })
    }

    fun logout(view: View) {
        launch({
            mLoginUserRepo.logout(
                ApiRepository.createRequestBody(
                    hashMapOf(
                        "authorizationClientType" to 4
                    )
                )
            )
        }, {

        }, {
            SPRepository.cleaLoginUserInfo()
            jump.postValue(1)
        })
    }
}