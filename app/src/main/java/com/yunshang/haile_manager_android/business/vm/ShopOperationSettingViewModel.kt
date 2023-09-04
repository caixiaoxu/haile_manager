package com.yunshang.haile_manager_android.business.vm

import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/4 18:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopOperationSettingViewModel : BaseViewModel() {

    val openSetting1: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
}