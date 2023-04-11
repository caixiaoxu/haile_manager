package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.*
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.data.entities.LoginEntity
import com.shangyun.haile_manager_android.data.entities.UserInfoEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.utils.RSAUtil
import com.shangyun.haile_manager_android.utils.UserPermissionUtils
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
    private val mRepo = ApiRepository.apiClient(LoginUserService::class.java)

    val loginInfo: MutableLiveData<LoginEntity> = MutableLiveData()
    val userInfo: MutableLiveData<UserInfoEntity?> = MutableLiveData(SPRepository.userInfo)
    val hasUserPermission: MutableLiveData<Boolean> = MutableLiveData(false)

    /** ---------------------用户权限------------------------- **/
    val hasProfitPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasProfitPermission() }
    val hasMessagePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMessagePermission() }
    val hasDevicePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDevicePermission() }
    val hasShopPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopPermission() }
    val hasOrderPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderPermission() }
    val hasPersonPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonPermission() }
    val hasMarketingPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingPermission() }
    val hasDistributionPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDistributionPermission() }


    /**
     * 密码登录
     */
    suspend fun loginForPassword(phone: String, password: String) {
        login(
            mutableMapOf(
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
        )
    }

    /**
     * 验证码登录
     */
    suspend fun loginForCode(phone: String, code: String) {
        login(
            mutableMapOf(
                "account" to phone,
                "verificationCode" to code,
                "loginType" to 2,
                "authorizationClientType" to 4
            )
        )
    }

    /**
     * 登录请求
     */
    suspend fun login(params: MutableMap<String, Any>) {
        val loginData =
            ApiRepository.dealApiResult(mRepo.login(ApiRepository.createRequestBody(params)))
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
    suspend fun requestUserInfo() {
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
    suspend fun requestUserPermissions() {
        val userPermissionData = ApiRepository.dealApiResult(mRepo.permissionByUser())
        Timber.d("用户权限请求成功$userPermissionData")
        userPermissionData?.let {
            SPRepository.userPermissions = it
            UserPermissionUtils.refreshData(it)
            hasUserPermission.postValue(true)
        }
    }
}