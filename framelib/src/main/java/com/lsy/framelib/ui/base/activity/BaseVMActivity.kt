package com.lsy.framelib.ui.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.lsy.framelib.intfs.ILoadingDialog
import com.lsy.framelib.ui.base.BaseApp
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.ui.weight.loading.LoadDialogMgr

/**
 * Title : Activity基类(构建ViewModel)
 * Author: Lsy
 * Date: 2023/3/16 14:25
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseVMActivity<T : ViewDataBinding, VM : BaseViewModel>(clz: Class<VM>) :
    BaseBindingActivity<T>(), ILoadingDialog {

    protected val mViewModel by lazy {
        getActivityViewModelProvider(this)[clz]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 界面跳转
        mViewModel.jump.observe(this) {
            jump(it)
        }

        // 请求等待条
        mViewModel.loadingStatus.observe(this) {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    /**
     * 用于处理跳转
     */
    open fun jump(type: Int) {}

    /**
     * 显示加载界面
     */
    override fun showLoading() {
        LoadDialogMgr.showLoadingDialog(this)
    }

    /**
     * 隐藏加载界面
     */
    override fun hideLoading() {
        LoadDialogMgr.hideLoadingDialog(this)
    }


    // 此getAppViewModelProvider函数，只给 共享的ViewModel用（例如：mSharedViewModel .... 等共享的ViewModel）
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (applicationContext as BaseApp).getAppViewModelProvider()
    }

    // 此getActivityViewModelProvider函数，给所有的 BaseActivity 子类用的 【ViewModel非共享区域】
    protected fun getActivityViewModelProvider(activity: AppCompatActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }
}