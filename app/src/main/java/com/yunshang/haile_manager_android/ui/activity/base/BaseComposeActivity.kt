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
    // 是否全屏幕显示
    protected open var isFullScreen = false

    // 屏幕方向
    protected open var orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    // 标签，用于区分activity
    protected open var activityTag: String? = null

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
                when (mViewModel.pageState) {
                    is PageState.InitState -> if (!(mViewModel.pageState as PageState.InitState).hideContent) content() else initPage(
                        mViewModel.pageState
                    )

                    is PageState.Loading -> if (!(mViewModel.pageState as PageState.Loading).hideContent) content() else loadingPage(
                        mViewModel.pageState
                    )

                    is PageState.LoadFailure -> loadFailurePage(mViewModel.pageState)
                    is PageState.EmptyData -> emptyDataPage(mViewModel.pageState)
                    else -> content()
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
    abstract fun content()

    /**
     * 初始状态布局
     */
    @Composable
    open fun initPage(state: PageState) {
        if (state is PageState.InitState && state.hideContent){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundPageColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "页面初始化中，请稍等",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.color_black_85)
                )
            }
        }
    }

    /**
     * 加载状态布局
     */
    @Composable
    open fun loadingPage(state: PageState) {
        if (state is PageState.Loading) {
            Dialog(onDismissRequest = { }) {
                Column(
                    modifier = Modifier.apply {
                        if (state.hideContent) {
                            this.fillMaxSize().background(BackgroundPageColor)
                        }
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_loading_origin_circle),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "数据请求中...",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.color_black_85)
                    )
                }
            }
        }
    }

    /**
     * 加载失败状态布局
     */
    @Composable
    open fun loadFailurePage(state: PageState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPageColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "数据加载失败",
                fontSize = 18.sp,
                color = colorResource(id = R.color.color_black_85)
            )
            if (state is PageState.LoadFailure && state.needReLoad) {
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton("重新加载", buttonState = WidgetState.EnableState) {
                    requestPageData()
                }
            }
        }
    }

    /**
     * 空数据布局
     */
    @Composable
    fun emptyDataPage(state: PageState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPageColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "数据为空",
                fontSize = 18.sp,
                color = colorResource(id = R.color.color_black_85)
            )
            if (state is PageState.EmptyData && state.needReLoad) {
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton("重新加载", buttonState = WidgetState.EnableState) {
                    requestPageData()
                }
            }
        }
    }

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