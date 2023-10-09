package com.yunshang.haile_manager_android.business.vm

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.lsy.framelib.ui.base.BaseViewModel
import com.yunshang.haile_manager_android.data.arguments.ShopPositionCreateParam

/**
 * Title :
 * Author: Lsy
 * Date: 2023/10/9 15:08
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ShopPositionCreateViewModel : BaseViewModel() {

    val positionParam = MutableLiveData(ShopPositionCreateParam())

    /**
     * 提交
     */
    fun submit(v: View){

    }
}