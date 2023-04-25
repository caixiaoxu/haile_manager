package com.lsy.framelib.ui.weight.loading

import androidx.fragment.app.FragmentActivity
import timber.log.Timber

/**
 * Title : 加载Dialog管理
 * Author: Lsy
 * Date: 2023/3/20 18:04
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object LoadDialogMgr {
    private const val LOAD_TIP_DIALOG_TAG = "LoadTipDialog"

    /**
     * 显示Loading弹窗
     */
    fun showLoadingDialog(activity: FragmentActivity) {
        val manager = activity.supportFragmentManager
        val fragment = manager.findFragmentByTag(LOAD_TIP_DIALOG_TAG)
        val loadingDialog = if (fragment is LoadingDialog) {
            fragment
        } else {
            LoadingDialog()
        }
        if (loadingDialog.isAdded) {
            return
        }
        manager.beginTransaction().remove(loadingDialog).add(loadingDialog, LOAD_TIP_DIALOG_TAG)
            .commitNowAllowingStateLoss()
    }

    /**
     * 隐藏Loading弹窗
     */
    fun hideLoadingDialog(activity: FragmentActivity) {
        val manager = activity.supportFragmentManager
        val fragment = manager.findFragmentByTag(LOAD_TIP_DIALOG_TAG)
        if (fragment is LoadingDialog) {
            fragment.dismissAllowingStateLoss()
        }
    }

//    fun showDialog() {
//        val activity = ActivityUtil.getTopActivity()
//        if (activity is FragmentActivity) {
//            showDialog(activity)
//        }
//    }
//
//    fun closeDialog() {
//        val activity = ActivityUtil.getTopActivity()
//        if (activity is FragmentActivity) {
//            closeDialog(activity)
//        }
//    }
}