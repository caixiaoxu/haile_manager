package com.yunshang.haile_manager_android.ui.view.component

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/4 17:07
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
sealed class WidgetState {
    data class InitState(val initName: String = "") : WidgetState()
    object DisableState : WidgetState()
    object EnableState : WidgetState()
}

sealed class WidgetSpec {
    object Large : WidgetSpec()
    object Medium : WidgetSpec()
    object Small : WidgetSpec()
}