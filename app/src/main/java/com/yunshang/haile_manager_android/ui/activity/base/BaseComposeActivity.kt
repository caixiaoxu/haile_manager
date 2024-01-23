package com.yunshang.haile_manager_android.ui.activity.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lsy.framelib.ui.base.BaseApp
import com.yunshang.haile_manager_android.business.vm.SharedViewModel
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.ui.theme.ComposeWidgetTheme

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/22 17:13
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseComposeActivity<VM : BaseComposeViewModel>(clz: Class<VM>) :
    ComponentActivity() {

    protected open var isFullScreen = false

    protected val mViewModel by lazy {
        getActivityViewModelProvider(this)[clz]
    }

    // 贯穿整个项目的（只会让App(Application)初始化一次）
    protected val mSharedViewModel: SharedViewModel by lazy {
        getAppViewModelProvider()[SharedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeWidgetTheme(dynamicColor = false) {
                WindowCompat.setDecorFitsSystemWindows(window, false)// 状态栏隐藏（可占用）
                if (isFullScreen) {
                    rememberSystemUiController().setStatusBarColor(
                        Color.Transparent,
                        true
                    ) // 状态栏背景：透明，字体颜色：黑色
                }
                when (mViewModel.pageState) {
                    is PageState.InitState -> if (!(mViewModel.pageState as PageState.InitState).hideContent) content() else initPage()
                    is PageState.Loading -> if (!(mViewModel.pageState as PageState.Loading).hideContent) content() else loadingPage(mViewModel.pageState)
                    is PageState.LoadFailure -> loadFailurePage(mViewModel.pageState)
                    is PageState.EmptyData -> emptyDataPage(mViewModel.pageState)
                    else -> content()
                }
            }
        }

        initEvent()

        requestData()
    }

    /**
     * 内容布局
     */
    @Composable
    abstract fun content()

    /**
     * 初始状态布局
     */
    @Composable
    open fun initPage() {
    }

    /**
     * 加载状态布局
     */
    @Composable
    open fun loadingPage(state: PageState) {

    }

    /**
     * 加载失败状态布局
     */
    @Composable
    open fun loadFailurePage(state: PageState) {

    }

    /**
     * 空数据布局
     */
    @Composable
    fun emptyDataPage(state: PageState) {

    }

    protected open fun initEvent() {}
    protected open fun requestData() {}

    // 此getAppViewModelProvider函数，只给 共享的ViewModel用（例如：mSharedViewModel .... 等共享的ViewModel）
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (applicationContext as BaseApp).getAppViewModelProvider()
    }

    // 此getActivityViewModelProvider函数，给所有的 BaseActivity 子类用的 【ViewModel非共享区域】
    protected fun getActivityViewModelProvider(activity: ComponentActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }
}