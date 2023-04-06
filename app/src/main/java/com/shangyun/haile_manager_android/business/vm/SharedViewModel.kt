package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.RequestBodyUtil
import com.shangyun.haile_manager_android.data.apiService.CommService
import com.shangyun.haile_manager_android.data.entities.LoginEntity
import com.shangyun.haile_manager_android.data.entities.UserInfoEntity
import com.shangyun.haile_manager_android.data.entities.UserPermissionEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.utils.RSAUtil
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/16 15:40
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class SharedViewModel : ViewModel() {
    private val mRepo = ApiRepository.apiClient(CommService::class.java)

    val loginInfo: MutableLiveData<LoginEntity> = MutableLiveData()
    val userInfo: MutableLiveData<UserInfoEntity> = MutableLiveData()
    val userPermissions: MutableLiveData<List<UserPermissionEntity>> = MutableLiveData()

    /**
     * 密码登录
     */
   suspend fun loginForPassword(phone: String, password: String) {
        val params: MutableMap<String, Any> = mutableMapOf(
            "account" to RSAUtil.encryptDataByPublicKey(
                phone.toByteArray(),
                RSAUtil.keyStrToPublicKey(RSAUtil.RSA_PUBLIC_KEY)
            ),
            "password" to RSAUtil.encryptDataByPublicKey(
                password.toByteArray(),
                RSAUtil.keyStrToPublicKey(RSAUtil.RSA_PUBLIC_KEY)
            ),
            "loginType" to 3,
            "authorizationClientType" to 4
        )

        val loginData =
            ApiRepository.dealApiResult(mRepo.login(RequestBodyUtil.createRequestBody(params)))
        Timber.d("登录接口请求成功$loginData")
        loginData?.let {
            SPRepository.loginInfo = it
            loginInfo.postValue(it)
        }

        requestUserInfo()
        requestUserPermissions()
    }

    /**
     * 请求用户信息
     */
    suspend fun requestUserInfo(){
        val userInfoData = ApiRepository.dealApiResult(mRepo.userInfo())
        Timber.d("用户信息请求成功$userInfoData")
        userInfoData?.let {
            SPRepository.userInfo = it
            userInfo.postValue(it)
        }
    }

    /**
     * 请求用户权限
     */
    suspend fun requestUserPermissions(){
        val userPermissionData = ApiRepository.dealApiResult(mRepo.permissionByUser())
        Timber.d("用户权限请求成功$userPermissionData")
        userPermissionData?.let {
            SPRepository.userPermissions = it
            userPermissions.postValue(it)
        }
    }
}