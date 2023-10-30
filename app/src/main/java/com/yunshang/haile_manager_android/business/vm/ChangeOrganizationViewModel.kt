package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.entities.LoginEntity
import com.yunshang.haile_manager_android.data.entities.RoleEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/19 16:39
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ChangeOrganizationViewModel : BaseViewModel() {
    private val mUserService = ApiRepository.apiClient(LoginUserService::class.java)

    val roleList: MutableLiveData<MutableList<RoleEntity>> by lazy {
        MutableLiveData()
    }

    /**
     * 请求角色列表
     */
    fun requestRoleList() {
        launch({
            ApiRepository.dealApiResult(
                mUserService.requestRoleInfoV2(ApiRepository.createRequestBody(hashMapOf()))
            )?.let {
                roleList.postValue(it)
            }
        })
    }

    /**
     * 切换角色
     */
    fun swapUserLogin(
        id: Int,
        mSharedViewModel: SharedViewModel,
        callback: () -> Unit
    ) {
        launch({
            ApiRepository.dealApiResult(
                mUserService.swapUserLogin(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "authorizationClientType" to 4,
                            "userId" to id
                        )
                    )
                )
            )?.let { login ->
                SPRepository.loginInfo = login
                mSharedViewModel.loginInfo.postValue(login)
                mSharedViewModel.swapUserInfo()
                withContext(Dispatchers.Main) {
                    callback()
                }
            }
        })
    }
}