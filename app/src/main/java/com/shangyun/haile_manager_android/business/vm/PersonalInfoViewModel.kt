package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.data.entities.LoginEntity
import com.shangyun.haile_manager_android.data.entities.RoleEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.data.model.SPRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
class PersonalInfoViewModel : BaseViewModel() {
    private val mUserService = ApiRepository.apiClient(LoginUserService::class.java)

    val roleList: MutableLiveData<List<RoleEntity>> by lazy {
        MutableLiveData()
    }

    /**
     * 上传头像
     */
    fun uploadHeadIcon(path: String, callback: (isSuccess: Boolean) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mUserService.updateLoadFile(
                    ApiRepository.createFileUploadBody(path)
                )
            )?.let {
                Timber.i("图片上传成功：$it")
                ApiRepository.dealApiResult(
                    mUserService.updateUserInfo(
                        ApiRepository.createRequestBody(hashMapOf("headImage" to it))
                    )
                )
                withContext(Dispatchers.Main) {
                    callback(true)
                }
            }
        }, {
            Timber.e("图片上传失败$it")
            callback(false)
        })
    }

    /**
     * 请求角色列表
     */
    fun requestRoleList() {
        launch({
            ApiRepository.dealApiResult(
                mUserService.requestRoleInfo(ApiRepository.createRequestBody(hashMapOf()))
            )?.let {
                roleList.postValue(it.filter { r ->
                    SPRepository.loginInfo?.userId?.let { id -> r.id != id } ?: false
                })
            }
        })
    }

    /**
     * 切换角色
     */
    fun swapUserLogin(id: Int, callback: (loginEntity: LoginEntity) -> Unit) {
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
            )?.let {
                callback(it)
            }
        })
    }
}