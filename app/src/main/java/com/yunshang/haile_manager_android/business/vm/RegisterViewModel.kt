package com.yunshang.haile_manager_android.business.vm

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.arguments.RegisterParams
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/21 17:00
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class RegisterViewModel : BaseViewModel() {
    private val mLoginRepo = ApiRepository.apiClient(LoginUserService::class.java)

    val registerParams: RegisterParams = RegisterParams()

    val isAgree: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun register(v: View) {
        if (registerParams.organizationName.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入商户名称")
            return
        }
        if (!StringUtils.checkShopName(registerParams.organizationName!!)) {
            SToast.showToast(v.context, "商户名称需2-50个中英文大小写")
            return
        }
        if (registerParams.realName.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入负责人名称")
            return
        }
        if (!StringUtils.checkName(registerParams.realName!!)) {
            SToast.showToast(v.context, "负责人名称需2-20个中英文大小写")
            return
        }
        if (registerParams.phone.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入手机号")
            return
        }
        if (registerParams.phone!!.length < 11) {
            SToast.showToast(v.context, "请输入正确的手机号")
            return
        }
        if (registerParams.code.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入验证码")
            return
        }
        if (registerParams.password.isNullOrEmpty()) {
            SToast.showToast(v.context, "请输入密码")
            return
        }
        if (!StringUtils.checkPassword(registerParams.password!!)) {
            SToast.showToast(v.context, "密码需6-12个中英文大小写和数字的组合")
            return
        }
        if (registerParams.passwordAgain.isNullOrEmpty()) {
            SToast.showToast(v.context, "请再次输入密码")
            return
        }
        if (registerParams.password != registerParams.passwordAgain) {
            SToast.showToast(v.context, "两次密码输入不一致")
            return
        }
        if (null == registerParams.provinceId || null == registerParams.cityId
            || null == registerParams.districtId
        ) {
            SToast.showToast(v.context, "请选择所属地区")
            return
        }

        launch({
            // 注册
            ApiRepository.dealApiResult(
                mLoginRepo.register(
                    ApiRepository.createRequestBody(GsonUtils.any2Json(registerParams))
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context, "注册成功，请登录")
            }
            // 注册成功
            jump.postValue(1)
        })
    }


    /**
     * 发送验证码
     */
    fun sendMsg(view: View) {
        if (registerParams.phone.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入手机号")
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mLoginRepo.sendRegisterCode(
                    ApiRepository.createRequestBody(
                        mutableMapOf(
                            "phone" to registerParams.phone,
                        )
                    )
                )
            )
            viewModelScope.launch(Dispatchers.Main) {
                countDownTimer(view as TextView)
            }
        })
    }

    // 验证码发送按钮内容
    private val defaultCodeTxt =
        com.lsy.framelib.utils.StringUtils.getString(R.string.login_code_send)
    private var timer: CountDownTimer? = null

    /**
     * 验证码倒计时
     */
    private fun countDownTimer(btn: TextView) {
        btn.isEnabled = false
        var num = 60
        timer?.cancel()
        timer = object : CountDownTimer((num + 1) * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                btn.text = "${num--}s"
            }

            override fun onFinish() {
                btn.text = defaultCodeTxt
                btn.isEnabled = true
            }
        }
        timer?.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        timer = null
    }
}