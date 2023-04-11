package com.shangyun.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.ui.activity.*

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
            R.mipmap.icon_personal_wallet, "钱包余额", null, MutableLiveData<String>(),
            WalletActivity::class.java
        ),
        PersonalItem(
            R.mipmap.icon_personal_balance,
            "余额明细",
            null,
            null,
            BalanceActivity::class.java
        ),
        null,
        PersonalItem(R.mipmap.icon_personal_income, "收入明细", null, null, IncomeActivity::class.java),
        null,
        PersonalItem(
            R.mipmap.icon_personal_bank_card,
            "银行卡",
            null,
            null,
            BankCardActivity::class.java
        ),
        PersonalItem(
            R.mipmap.icon_personal_real_name,
            "实名认证",
            MutableLiveData<String>(),
            null,
            RealNameActivity::class.java
        ),
        null,
        PersonalItem(
            R.mipmap.icon_personal_setting,
            "设置",
            MutableLiveData<String>(),
            null,
            SettingActivity::class.java
        ),
        null,
    )

    data class PersonalItem(
        val icon: Int,
        val title: String,
        val tag: MutableLiveData<String>?,
        val value: MutableLiveData<String>?,
        val clz: Class<*>
    )
}