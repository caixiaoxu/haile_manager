package com.shangyun.haile_manager_android.business.vm

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.shangyun.haile_manager_android.ui.activity.MainActivity
import com.shangyun.haile_manager_android.utils.StringUtils
import timber.log.Timber

/**
 * Title : 密码登录的viewmodel
 * Author: Lsy
 * Date: 2023/4/4 13:57
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class LoginForPasswordViewModel : BaseViewModel() {

    // 手机号
    val phone: MutableLiveData<String> = MutableLiveData()

    // 密码
    val password: MutableLiveData<String> = MutableLiveData()

    // 同意协议
    val isAgree: MutableLiveData<Boolean> = MutableLiveData(false)

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(phone) {
            value = checkSubmit()
        }
        addSource(password) {
            value = checkSubmit()
        }
        addSource(isAgree) {
            value = checkSubmit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !TextUtils.isEmpty(phone.value) && !TextUtils.isEmpty(password.value) && isAgree.value!!

    /**
     * 登录
     */
    fun login(view: View, sharedView: SharedViewModel) {
        if (phone.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入手机号")
            return
        }
        if (password.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入密码")
            return
        }

        if (!StringUtils.checkPassword(password.value)) {
            SToast.showToast(view.context, "密码必须包含6-12位大小写字母和数字")
            return
        }

        if (true != isAgree.value) {
            SToast.showToast(view.context, "请阅读并同意协议")
            return
        }

        launch(
            {
                sharedView.loginForPassword(phone.value!!, password.value!!)
                view.context.startActivity(Intent(view.context, MainActivity::class.java))
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