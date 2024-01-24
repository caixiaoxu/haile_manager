package com.yunshang.haile_manager_android.business.vm.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.business.UnPeekLiveData
import com.lsy.framelib.network.exception.CommonCustomException
import com.lsy.framelib.utils.SToast
import com.yunshang.haile_manager_android.ui.activity.base.PageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

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

    /**
     * ViewModel异步请求
     */
    fun launch(
        block: suspend () -> Unit,           // 异步操作
        error: (suspend (Throwable) -> Unit)? = null,  // 操作异常
        complete: (suspend () -> Unit)? = null,        // 操作完成

    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                error?.invoke(e) ?: withContext(Dispatchers.Main) {
                    Timber.d("请求失败或异常$e")
                }
            } finally {
                complete?.invoke() ?: withContext(Dispatchers.Main) {
                    Timber.d("请求结束")
                }
            }
        }
    }
}