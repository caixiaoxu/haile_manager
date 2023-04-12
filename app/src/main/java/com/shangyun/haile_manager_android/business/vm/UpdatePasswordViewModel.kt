package com.shangyun.haile_manager_android.business.vm

import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.utils.ActivityManagerUtils
import com.shangyun.haile_manager_android.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/6 17:46
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class UpdatePasswordViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(LoginUserService::class.java)

    // 新密码
    val oldPwd: MutableLiveData<String> = MutableLiveData()
    val newPwd: MutableLiveData<String> = MutableLiveData()

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(oldPwd) {
            value = checkSubmit()
        }
        addSource(newPwd) {
            value = checkSubmit()
        }
    }

    private var timer: CountDownTimer? = null

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =  !oldPwd.value.isNullOrEmpty() && !newPwd.value.isNullOrEmpty()

    /**
     * 登录
     */
    fun updatePassword(view: View) {
        if (oldPwd.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入旧密码")
            return
        }

        if (newPwd.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入新密码")
            return
        }

        if (!StringUtils.checkPassword(oldPwd.value)) {
            SToast.showToast(view.context, "密码必须包含6-12位大小写字母和数字")
            return
        }

        launch(
            {
                val loginData =
                    ApiRepository.dealApiResult(
                        mRepo.updatePassword(
                            ApiRepository.createRequestBody(
                                mutableMapOf(
                                    "oldPassword" to oldPwd.value!!,
                                    "password" to newPwd.value!!
                                )
                            )
                        )
                    )
                Timber.d("修改密码接口请求成功$loginData")
                ActivityManagerUtils.clearLoginInfoGoLogin()
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            {
                Timber.d("请求结束")
            })
    }
}