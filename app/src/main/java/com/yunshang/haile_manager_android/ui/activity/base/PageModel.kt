package com.yunshang.haile_manager_android.ui.activity.base

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/22 17:38
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
sealed class PageState {
    /**
     * 初始化状态
     * @param hideContent 未返回数据前是否隐藏内容布局
     */
    data class InitState(val hideContent: Boolean) : PageState()

    /**
     * 加载状态
     * @param hideContent 未返回数据前是否隐藏内容布局
     */
    data class Loading(val hideContent: Boolean) : PageState()

    object LoadData : PageState()

    /**
     * 加载失败状态
     * @param needReLoad 是否显示重新加载按钮
     */
    data class LoadFailure(val needReLoad: Boolean) : PageState()

    /**
     * 数据为空状态
     * @param needReLoad 是否显示重新加载按钮
     */
    data class EmptyData(val needReLoad: Boolean) : PageState()
}