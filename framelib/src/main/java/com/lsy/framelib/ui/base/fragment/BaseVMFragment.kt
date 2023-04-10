package com.lsy.framelib.ui.base.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.lsy.framelib.intfs.ILoadingDialog
import com.lsy.framelib.ui.base.BaseApp
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.ui.weight.loading.LoadDialogMgr

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/16 14:26
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseVMFragment<T : ViewDataBinding,VM : BaseViewModel> :  BaseBindingFragment<T>(),
    ILoadingDialog {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 界面跳转
        getVM().jump.observe(viewLifecycleOwner) {
            jump(it)
        }

        // 请求等待条
        getVM().loadingStatus.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }


    /**
     * 获取子类的ViewModel
     */
    abstract fun getVM(): VM

    /**
     * 用于处理跳转
     */
    open fun jump(type: Int) {}

    /**
     * 显示加载界面
     */
    override fun showLoading() {
        LoadDialogMgr.showLoadingDialog(requireActivity())
    }

    /**
     * 隐藏加载界面
     */
    override fun hideLoading() {
        LoadDialogMgr.hideLoadingDialog(requireActivity())
    }

    // 给当前BaseFragment用的【共享区域的ViewModel】
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (requireActivity().applicationContext as BaseApp).getAppViewModelProvider()
    }

    // 给所有的 子fragment提供的函数，可以顺利的拿到 ViewModel 【非共享区域的ViewModel】
    protected fun getFragmentViewModelProvider(fragment: Fragment): ViewModelProvider {
        return ViewModelProvider(fragment, fragment.defaultViewModelProviderFactory)
    }

    // 给所有的 子fragment提供的函数，可以顺利的拿到 ViewModel 【非共享区域的ViewModel】
    protected fun getActivityViewModelProvider(activity: FragmentActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }
}