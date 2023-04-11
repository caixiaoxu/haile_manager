package com.shangyun.haile_manager_android.business.vm

import android.view.View
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.model.SPRepository

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
    private val mRepo = ApiRepository.apiClient(LoginUserService::class.java)

    fun logout(view: View) {
        launch({
            mRepo.logout(
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