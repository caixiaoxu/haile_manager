package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.entities.LoginEntity
import com.yunshang.haile_manager_android.data.entities.RoleEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
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
    private val mCommonService = ApiRepository.apiClient(CommonService::class.java)
    private val mUserService = ApiRepository.apiClient(LoginUserService::class.java)

    /**
     * 上传头像
     */
    fun uploadHeadIcon(path: String, callback: (isSuccess: Boolean) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mCommonService.updateLoadFile(
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
            withContext(Dispatchers.Main) {
                callback(false)
            }
        })
    }
}