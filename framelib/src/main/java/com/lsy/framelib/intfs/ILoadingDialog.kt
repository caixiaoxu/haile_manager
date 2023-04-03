package com.lsy.framelib.intfs

/**
 * Title : 加载等待弹窗界面接口
 * Author: Lsy
 * Date: 2023/3/21 10:50
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface ILoadingDialog {

    /**
     * 显示Loading
     */
    fun showLoading()

    /**
     * 隐藏Loading
     */
    fun hideLoading()
}