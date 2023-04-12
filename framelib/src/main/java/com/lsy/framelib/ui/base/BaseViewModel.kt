package com.lsy.framelib.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.business.UnPeekLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Title : BaseViewModel
 * Author: Lsy
 * Date: 2023/3/16 17:45
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
open class BaseViewModel : ViewModel() {
    // 用于跳转
    val jump = MutableLiveData<Int>()

    val loadingStatus = UnPeekLiveData<Boolean>()

    /**
     * ViewModel异常处理方法
     */
    fun launch(
        block: suspend () -> Unit,           // 异步操作
        error: suspend (Throwable) -> Unit,  // 操作异常
        complete: suspend () -> Unit,        // 操作完成
        showLoading: Boolean = true          // 是否显示加载弹窗
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            //显示加载弹窗
            if (showLoading)
                loadingStatus.postValue(true)
            try {
                block()
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    error(e)
                }
            } finally {
                withContext(Dispatchers.Main){
                    complete()
                }
                //隐藏加载弹窗
                if (showLoading)
                    loadingStatus.postValue(false)
            }
        }
    }
}