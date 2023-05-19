package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.lsy.framelib.network.exception.CommonCustomException
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.data.entities.*
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.utils.RSAUtil
import com.shangyun.haile_manager_android.utils.UserPermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    /** -----------LiveData.map 存在一个问题，不调用observable，不会初化始----------- **/
    /** ---------------------用户权限------------------------- **/

    val hasProfitPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasProfitPermission() }
    val hasMessagePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMessagePermission() }

    /** ---------------------设备权限------------------------- **/
    val hasDevicePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDevicePermission() }
    val hasDeviceListPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceListPermission() }
    val hasDeviceInfoPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceInfoPermission() }
    val hasDeviceProfitPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceProfitPermission() }
    val hasDeviceAddPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceAddPermission() }
    val hasDeviceDeletePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceDeletePermission() }
    val hasDeviceResetPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceResetPermission() }
    val hasDeviceStartPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceStartPermission() }
    val hasDeviceCleanPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceCleanPermission() }
    val hasDeviceQrcodePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceQrcodePermission() }
    val hasDeviceUpdatePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceUpdatePermission() }
    val hasDeviceAppointmentPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDeviceAppointmentPermission() }

    /** ---------------------设备权限------------------------- **/

    /** ---------------------店铺权限------------------------- **/
    val hasShopPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopPermission() }
    val hasShopListPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopListPermission() }
    val hasShopInfoPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopInfoPermission() }
    val hasShopProfitPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopProfitPermission() }
    val hasShopAddPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopAddPermission() }
    val hasShopDeletePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopDeletePermission() }
    val hasShopUpdatePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopUpdatePermission() }
    val hasShopAppointPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasShopAppointPermission() }
    /** ---------------------店铺权限------------------------- **/
    /** ---------------------订单权限------------------------- **/
    val hasOrderPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderPermission() }
    val hasOrderListPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderListPermission() }
    val hasOrderInfoPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderInfoPermission() }
    val hasOrderRefundPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderRefundPermission() }
    val hasOrderStartPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderStartPermission() }
    val hasOrderResetPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderResetPermission() }
    val hasOrderCompensatePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasOrderCompensatePermission() }
    /** ---------------------订单权限------------------------- **/
    /** ---------------------人员权限------------------------- **/
    val hasPersonPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonPermission() }
    val hasPersonAddPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonAddPermission() }
    val hasPersonListPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonListPermission() }
    val hasPersonInfoPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonInfoPermission() }
    val hasPersonUpdatePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonUpdatePermission() }
    val hasPersonDeletePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasPersonDeletePermission() }
    /** ---------------------人员权限------------------------- **/
    /** ---------------------折扣权限------------------------- **/
    val hasMarketingPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingPermission() }
    val hasMarketingListPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingListPermission() }
    val hasMarketingInfoPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingInfoPermission() }
    val hasMarketingAddPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingAddPermission() }
    val hasMarketingUpdatePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingUpdatePermission() }
    val hasMarketingDeletePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasMarketingDeletePermission() }
    /** ---------------------折扣权限------------------------- **/
    /** ---------------------分账权限------------------------- **/
    val hasDistributionPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDistributionPermission() }
    val hasDistributionListPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDistributionListPermission() }
    val hasDistributionAddPermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDistributionAddPermission() }
    val hasDistributionDeletePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDistributionDeletePermission() }
    val hasDistributionUpdatePermission: LiveData<Boolean> =
        hasUserPermission.map { UserPermissionUtils.hasDistributionUpdatePermission() }
    /** ---------------------分账权限------------------------- **/
    /** ---------------------用户权限------------------------- **/

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
            ), PASSWORD, password
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
            ), CODE
        )
    }

    /**
     * 登录请求
     */
    suspend fun login(
        params: MutableMap<String, Any>,
        @LoginType loginType: Int,
        password: String? = null,
        isCheckToken: Boolean = false,
    ) {
        // 公共参数
        params["authorizationClientType"] = 4
        //区分是否是检验token接口
        val loginData = if (isCheckToken) {
            ApiRepository.dealApiResult(mRepo.checkToken(ApiRepository.createRequestBody(params)))
        } else {
            ApiRepository.dealApiResult(mRepo.login(ApiRepository.createRequestBody(params)))
        }
        Timber.d("登录接口请求成功$loginData")
        loginData?.let {
            SPRepository.loginInfo = it
            loginInfo.postValue(it)
        } ?: throw CommonCustomException(-1, "返回数据为空")

        requestUserInfo()

        // 当请求到登录信息和用户信息后，缓存到本地，用于切换账号
        SPRepository.changeUser?.let { list ->
            //移除之前的缓存
            val index: Int =
                list.indexOfFirst { it.loginInfo.userId == SPRepository.loginInfo!!.userId }
            if (-1 != index) {
                list.removeAt(index)
            }
            list.add(
                ChangeUserEntity(
                    loginType,
                    password,
                    SPRepository.loginInfo!!,
                    SPRepository.userInfo!!
                )
            )
            SPRepository.changeUser = list
        }
        requestUserPermissions()
    }

    /**
     * 请求用户权限
     */
    fun requestUserInfoAsync() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                requestUserInfo()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 请求用户信息
     */
    private suspend fun requestUserInfo() {
        val userInfoData = ApiRepository.dealApiResult(mRepo.userInfo())
        Timber.d("用户信息请求成功$userInfoData")
        userInfoData?.let {
            SPRepository.userInfo = it
            userInfo.postValue(it)
        } ?: throw CommonCustomException(-1, "返回数据为空")
    }

    /**
     * 请求用户权限
     */
    fun requestUserPermissionsAsync() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                requestUserPermissions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 请求用户权限
     */
    private suspend fun requestUserPermissions() {
        val userPermissionData = ApiRepository.dealApiResult(mRepo.permissionByUser())
        Timber.d("用户权限请求成功$userPermissionData")
        userPermissionData?.let {
            SPRepository.userPermissions = it
            UserPermissionUtils.refreshData(it)
            hasUserPermission.postValue(true)
        }
    }
}