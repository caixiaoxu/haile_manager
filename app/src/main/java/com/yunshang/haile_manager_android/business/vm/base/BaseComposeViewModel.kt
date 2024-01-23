package com.yunshang.haile_manager_android.business.vm.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yunshang.haile_manager_android.ui.activity.base.PageState

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/22 17:45
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
open class BaseComposeViewModel : ViewModel() {
    var pageState by mutableStateOf<PageState>(PageState.InitState(false))

}