package com.yunshang.haile_manager_android.ui.activity.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.lsy.framelib.ui.base.BaseApp
import com.lsy.framelib.utils.AppManager
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.SharedViewModel
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.ui.theme.BackgroundPageColor
import com.yunshang.haile_manager_android.ui.theme.ComposeWidgetTheme
import com.yunshang.haile_manager_android.ui.view.component.PageStateComponent
import com.yunshang.haile_manager_android.ui.view.component.WidgetState
import com.yunshang.haile_manager_android.ui.view.component.button.PrimaryButton

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
    // 标签，用于区分activity
    protected open var activityTag: String? = null

    // 是否全屏幕显示
    protected open var isFullScreen = false

    // 屏幕方向
    protected open var orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    // 初始状态布局
    open var initView: (@Composable (state: PageState) -> Unit)? = null

    // 加载状态布局
    open var loadingView: (@Composable (state: PageState) -> Unit)? = null

    // 加载失败状态布局
    open var loadFailureView: (@Composable (state: PageState) -> Unit)? = null

    // 空数据状态界面
    open var emptyDataView: (@Composable (state: PageState) -> Unit)? = null

    protected val mViewModel by lazy {
        getActivityViewModelProvider(this)[clz]
    }

    // 贯穿整个项目的（只会让App(Application)初始化一次）
    protected val mSharedViewModel: SharedViewModel by lazy {
        getAppViewModelProvider()[SharedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.addActivity(activityTag, this)
        // 屏幕方向
        setScreenOrientation()
        setContent {
            ComposeWidgetTheme(dynamicColor = false) {
                WindowCompat.setDecorFitsSystemWindows(window, false)// 状态栏隐藏（可占用）
                if (isFullScreen) {
                    rememberSystemUiController().setStatusBarColor(
                        Color.Transparent,
                        true
                    ) // 状态栏背景：透明，字体颜色：黑色
                }
                PageStateComponent(
                    pageState = mViewModel.pageState,
                    initView = initView,
                    loadingView = loadingView,
                    loadFailureView = loadFailureView,
                    emptyDataView = emptyDataView,
                    requestPageData = { requestPageData() }
                ) {
                    ContentPage()
                }
            }
        }

        initEvent()

        requestPageData()
        requestIdleData()
    }

    override fun onDestroy() {
        AppManager.finishActivity(activityTag, this)
        super.onDestroy()
    }

    /**
     * 设置屏幕方向
     */
    private fun setScreenOrientation() {
        if (this.resources.configuration.orientation != orientation) {
            requestedOrientation = orientation
        }
    }

    /**
     * 内容布局
     */
    @Composable
    abstract fun ContentPage()

    /**
     * 初始化监听事件
     */
    protected open fun initEvent() {}

    /**
     * 请求界面数据
     */
    protected open fun requestPageData() {}

    /**
     * 请求空闲数据
     */
    protected open fun requestIdleData() {

    }

    // 此getAppViewModelProvider函数，只给 共享的ViewModel用（例如：mSharedViewModel .... 等共享的ViewModel）
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (applicationContext as BaseApp).getAppViewModelProvider()
    }

    // 此getActivityViewModelProvider函数，给所有的 BaseActivity 子类用的 【ViewModel非共享区域】
    protected fun getActivityViewModelProvider(activity: ComponentActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }
}