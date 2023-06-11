package com.yunshang.haile_manager_android.business.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CapitalService
import com.yunshang.haile_manager_android.business.apiService.LoginUserService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.entities.BalanceTotalEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.activity.*
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
            R.mipmap.icon_personal_income,
            R.string.income,
            null,
            null,
            IncomeActivity::class.java,
            Bundle().apply {
                putInt(IncomeActivity.ProfitType, 3)
            }
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
            MutableLiveData<String>(StringUtils.getStringArray(R.array.auth_status_arr)[1]),
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
        val clz: Class<*>,
        var bundle: Bundle? = null
    )

    private var balanceTotal: BalanceTotalEntity? = null

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
        balanceTotal = ApiRepository.dealApiResult(mCapitalRepo.requestBalance())
        balanceTotal?.let {
            personalItems.find { item -> item?.title == R.string.wallet }?.run {
                value?.postValue(it.totalAmount)
                bundle = IntentParams.WalletParams.pack(it.totalAmount, false)
            }
        }
    }

    fun requestRealNameAuthAsync() {
        launch({
            requestRealNameAuth()
        })
    }


    private suspend fun requestRealNameAuth() {
        val authInfo = ApiRepository.dealApiResult(mUserRepo.requestRealNameAuthDetail())
        authInfo?.let {
            personalItems.find { item -> item?.title == R.string.real_name }?.run {
                tag?.postValue(StringUtils.getStringArray(R.array.auth_status_arr)[it.status])
                bundle = IntentParams.RealNameAuthParams.pack(it)
            }
        }

        balanceTotal?.let {
            personalItems.find { item -> item?.title == R.string.wallet }?.run {
                bundle = IntentParams.WalletParams.pack(it.totalAmount, 3 == authInfo?.status)
            }
        }
    }
}