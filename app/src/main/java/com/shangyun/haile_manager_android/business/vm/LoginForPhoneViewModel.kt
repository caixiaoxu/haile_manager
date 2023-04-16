package com.shangyun.haile_manager_android.business.vm

import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.LoginUserService
import com.shangyun.haile_manager_android.business.event.BusEvents
import com.shangyun.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
class LoginForPhoneViewModel : BaseViewModel() {
    private val mRepo = ApiRepository.apiClient(LoginUserService::class.java)

    // 手机号
    val phone: MutableLiveData<String> = MutableLiveData()

    // 验证码
    val code: MutableLiveData<String> = MutableLiveData()

    // 验证码发送按钮内容
    private val defaultCodeTxt = StringUtils.getString(R.string.login_code_send)

    // 同意协议
    val isAgree: MutableLiveData<Boolean> = MutableLiveData(false)

    // 是否可提交
    val canSubmit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(phone) {
            value = checkSubmit()
        }
        addSource(code) {
            value = checkSubmit()
        }
        addSource(isAgree) {
            value = checkSubmit()
        }
    }

    private var timer: CountDownTimer? = null

    /**
     * 检测是否可提交
     */
    private fun checkSubmit(): Boolean =
        !phone.value.isNullOrEmpty() && !code.value.isNullOrEmpty() && isAgree.value!!

    /**
     * 登录
     */
    fun login(view: View, sharedView: SharedViewModel) {
        if (phone.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入手机号")
            return
        }
        if (code.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入验证码")
            return
        }

        if (true != isAgree.value) {
            SToast.showToast(view.context, "请阅读并同意协议")
            return
        }

        launch(
            {
                sharedView.loginForCode(phone.value!!, code.value!!)
                LiveDataBus.post(BusEvents.LOGIN_STATUS, true)
                jump.postValue(1)
            },
            {
                it.message?.let { it1 -> SToast.showToast(msg = it1) }
                Timber.d("请求失败或异常$it")
            },
            {
                Timber.d("请求结束")
            })
    }

    /**
     * 发送验证码
     */
    fun sendMsg(view: View) {
        if (phone.value.isNullOrEmpty()) {
            SToast.showToast(view.context, "请先输入手机号")
            return
        }

        launch({
            ApiRepository.dealApiResult(
                mRepo.sendCode(
                    ApiRepository.createRequestBody(
                        mutableMapOf(
                            "target" to phone.value!!,
                            "sendType" to 1,//1:短信；2:邮件
                            "method" to 1 //1:登录；2:修改密码
                        )
                    )
                )
            )
            viewModelScope.launch(Dispatchers.Main) {
                countDownTimer(view as Button)
            }
        }, {
            it.message?.let { it1 -> SToast.showToast(msg = it1) }
            Timber.d("请求失败或异常$it")
        }, {
            Timber.d("请求结束")
        })
    }

    /**
     * 验证码倒计时
     */
    private fun countDownTimer(btn: Button) {
        btn.isEnabled = false
        var num = 60
        if (null == timer) {
            timer = object : CountDownTimer((num + 1) * 1000L, 1000L) {

                override fun onTick(millisUntilFinished: Long) {
                    btn.text = "${num--}s"
                }

                override fun onFinish() {
                    btn.text = defaultCodeTxt
                    btn.isEnabled = true
                }
            }
        }
        timer?.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.onFinish()
    }
}