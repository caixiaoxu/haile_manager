package com.shangyun.haile_manager_android.business.vm

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.ui.activity.*
import com.shangyun.haile_manager_android.ui.activity.personal.*

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

    val personalItems = arrayOf(
        null,
        PersonalItem(
            R.mipmap.icon_personal_wallet, R.string.wallet, null, MutableLiveData<String>(),
            WalletActivity::class.java
        ),
        PersonalItem(
            R.mipmap.icon_personal_balance,
            R.string.balance,
            null,
            null,
            BalanceActivity::class.java
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
        PersonalItem(
            R.mipmap.icon_personal_bank_card,
            R.string.bank_card,
            null,
            null,
            BankCardActivity::class.java
        ),
        PersonalItem(
            R.mipmap.icon_personal_real_name,
            R.string.real_name,
            MutableLiveData<String>(),
            null,
            RealNameActivity::class.java
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
        val bundle: Bundle? = null
    )
}