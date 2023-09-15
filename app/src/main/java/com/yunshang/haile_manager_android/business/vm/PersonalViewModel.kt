package com.yunshang.haile_manager_android.business.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.activity.personal.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/7 13:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class PersonalViewModel : BaseViewModel() {
    private val mCapitalRepo = ApiRepository.apiClient(CapitalService::class.java)
    private val mUserRepo = ApiRepository.apiClient(LoginUserService::class.java)

    val personalItems = arrayOf(
        null,
        PersonalItem(
            R.mipmap.icon_personal_wallet, R.string.wallet, null, MutableLiveData<String>(),
            WalletActivity::class.java
        ),
        null,
        PersonalItem(
            R.mipmap.icon_personal_income_calendar,
            R.string.income_calendar,
            null,
            null,
            IncomeCalendarActivity::class.java,
            Bundle().apply {
                putInt(IncomeCalendarActivity.ProfitType, 3)
            }
        ),
        PersonalItem(
            R.mipmap.icon_personal_income,
            R.string.income_statistics,
            null,
            null,
            IncomeStatisticsActivity::class.java,
        ),
        null,
//        PersonalItem(
//            R.mipmap.icon_personal_bank_card,
//            R.string.bank_card,
//            null,
//            null,
//            BankCardActivity::class.java
//        ),
        PersonalItem(
            R.mipmap.icon_personal_real_name,
            R.string.real_name,
            MutableLiveData<String>(StringUtils.getStringArray(R.array.verify_status_arr)[0]),
            null,
            RealNameAuthActivity::class.java
        ),
        null,
        PersonalItem(
            R.mipmap.icon_personal_setting,
            R.string.setting,
            MutableLiveData<String>(),
            null,
            SettingActivity::class.java
        ),
        null,
    )

    data class PersonalItem(
        val icon: Int,
        val title: Int,
        val tag: MutableLiveData<String>?,
        val value: MutableLiveData<String>?,
        var clz: Class<*>? = null,
        var bundle: Bundle? = null,
        val isShow: Boolean = true,
    )

    fun requestData() {
        launch({
            requestBalance()
            requestRealNameAuth()
        })
    }

    fun requestBalanceAsync() {
        launch({
            requestBalance()
        })
    }

    private suspend fun requestBalance() {
        ApiRepository.dealApiResult(mCapitalRepo.requestBalance())?.let {
            personalItems.find { item -> item?.title == R.string.wallet }?.run {
                value?.postValue(it.totalAmount)
            }
        }
    }

    fun requestRealNameAuthAsync() {
        launch({
            requestRealNameAuth()
        }, {})
    }

    private suspend fun requestRealNameAuth() {
        ApiRepository.dealApiResult(mUserRepo.requestRealNameAuthDetail())?.let {
            personalItems.find { item -> item?.title == R.string.real_name }?.run {
                val params = IntentParams.RealNameAuthParams.pack(it)
                it.status?.let { status ->
                    tag?.postValue(StringUtils.getStringArray(R.array.verify_status_arr)[status - 1])
                    if (status < 2) {
                        clz = BindSmsVerifyActivity::class.java
                        bundle = params.apply { putAll(IntentParams.BindSmsVerifyParams.pack(2)) }
                    } else null
                } ?: run {
                    clz = RealNameAuthActivity::class.java
                    bundle = params
                }
            }
            personalItems.find { item -> item?.title == R.string.wallet }?.run {
                bundle = IntentParams.WalletParams.pack(it)
            }
            personalItems.find { item -> item?.title == R.string.bank_card }?.run {
                bundle = IntentParams.RealNameAuthParams.pack(it)
            }
        }
    }
}