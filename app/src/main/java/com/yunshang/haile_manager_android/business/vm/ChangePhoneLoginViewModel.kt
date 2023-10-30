package com.yunshang.haile_manager_android.business.vm

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/26 10:09
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ChangePhoneLoginViewModel : BaseViewModel() {
    private val mLoginUserRepo = ApiRepository.apiClient(LoginUserService::class.java)

    val step: MutableLiveData<Int> = MutableLiveData(0)

    // 验证码发送按钮内容
    private val defaultCodeTxt = StringUtils.getString(R.string.login_code_send)

    val oldPhone: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val oldPhoneVal: LiveData<String> = oldPhone.map {
        it?.let {
            if (it.length > 7) com.yunshang.haile_manager_android.utils.StringUtils.formatPhone(
                it
            ) else it
        } ?: ""
    }

    val step1Code: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canStep1Submit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(oldPhone) {
            value = checkStep1Submit()
        }
        addSource(step1Code) {
            value = checkStep1Submit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkStep1Submit(): Boolean =
        (!oldPhone.value.isNullOrEmpty() && 11 == oldPhone.value!!.length) && !step1Code.value.isNullOrEmpty()

    fun sendStep1Code(v: View) {
        if (oldPhone.value.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mLoginUserRepo.sendChangeLoginPhoneCode(
                    ApiRepository.createRequestBody(
                        hashMapOf("phone" to oldPhone.value, "type" to 1)
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context,R.string.send_success)
                countDownTimer(v as TextView)
            }
        })
    }

    fun submitStep1(v: View) {
        launch({
            ApiRepository.dealApiResult(
                mLoginUserRepo.checkChangeLoginPhoneCode(
                    ApiRepository.createRequestBody(
                        hashMapOf("phone" to oldPhone.value, "code" to step1Code.value)
                    )
                )
            )

            step.postValue(1)
        })
    }

    val newPhone: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    val step2Code: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 是否可提交
    val canStep2Submit: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
        addSource(newPhone) {
            value = checkStep2Submit()
        }
        addSource(step2Code) {
            value = checkStep2Submit()
        }
    }

    /**
     * 检测是否可提交
     */
    private fun checkStep2Submit(): Boolean =
        (!newPhone.value.isNullOrEmpty() && 11 == newPhone.value!!.length) && !step2Code.value.isNullOrEmpty()

    fun sendStep2Code(v: View, callBack: (success: Boolean, msg: String?) -> Unit) {
        if (newPhone.value.isNullOrEmpty()) return
        launch({
            ApiRepository.dealApiResult(
                mLoginUserRepo.sendChangeLoginPhoneCode(
                    ApiRepository.createRequestBody(
                        hashMapOf("phone" to newPhone.value, "type" to 2)
                    )
                )
            )
            withContext(Dispatchers.Main) {
                SToast.showToast(v.context,R.string.send_success)
                countDownTimer(v as TextView)
            }
        }, {
            withContext(Dispatchers.Main) {
                if (it is CommonCustomException) {
                    if (60003 == it.code)
                        callBack(false, it.message)
                    else
                        it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(v.context, "网络开小差~")
                }
                Timber.d("请求失败或异常$it")
            }
        })
    }

    fun submitStep2(v: View, callBack: (success: Boolean, msg: String?) -> Unit) {
        launch({
            ApiRepository.dealApiResult(
                mLoginUserRepo.changeLoginPhone(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "originalCode" to step1Code.value,
                            "phone" to newPhone.value,
                            "code" to step2Code.value
                        )
                    )
                )
            )
            //从切换账号列表中找到旧账号数据，更换手机号并替换
            SPRepository.changeUser?.find { item -> item.userInfo.userInfo.phone == oldPhone.value!! }
                ?.let {
                    it.userInfo.userInfo.phone = newPhone.value!!
                    SPRepository.changeUser = SPRepository.changeUser
                }
            SPRepository.cleaLoginUserInfo()
            withContext(Dispatchers.Main) {
                callBack(true, "")
            }
        }, {
            withContext(Dispatchers.Main) {
                if (it is CommonCustomException) {
                    if (60003 == it.code)
                        callBack(false, it.message)
                    else
                        it.message?.let { it1 -> SToast.showToast(msg = it1) }
                } else {
                    SToast.showToast(v.context, "网络开小差~")
                }
                Timber.d("请求失败或异常$it")
            }
        })
    }

    private var timerStep1: CountDownTimer? = null
    private var timerStep2: CountDownTimer? = null

    /**
     * 验证码倒计时
     */
    private fun countDownTimer(btn: TextView) {
        var timer = if (0 == step.value) timerStep1 else timerStep2

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
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        timerStep1?.cancel()
        timerStep1 = null
        timerStep2?.cancel()
        timerStep2 = null
    }
}