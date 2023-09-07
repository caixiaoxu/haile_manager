package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.data.entities.BankCardDetailEntity
import com.yunshang.haile_manager_android.data.entities.RealNameAuthDetailEntity
import com.yunshang.haile_manager_android.ui.fragment.BankCardBindCardInfoFragment
import com.yunshang.haile_manager_android.ui.fragment.BankCardBindShopInfoFragment

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/5 18:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BankCardBindViewModel : BaseViewModel() {

    var authCode: String? = null

    val authInfo: MutableLiveData<RealNameAuthDetailEntity> by lazy {
        MutableLiveData()
    }

    val fragments = listOf(
        BankCardBindCardInfoFragment(),
        BankCardBindShopInfoFragment(),
    )

    val bindPage: MutableLiveData<Int> = MutableLiveData(0)

    val bankCardParams: MutableLiveData<BankCardDetailEntity> by lazy {
        MutableLiveData()
    }

    fun nextOrSubmit(v: View) {
        if (0 == bindPage.value) {
            bindPage.value = 1
        } else {

        }
    }
}