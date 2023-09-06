package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel

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

    val bindPage:MutableLiveData<Int> = MutableLiveData(0)


    fun nextOrSubmit(v: View) {
        if (0 == bindPage.value){
            bindPage.value = 1
        } else {

        }
    }
}