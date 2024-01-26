package com.yunshang.haile_manager_android.ui.view.component

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.ui.activity.base.PageState
import com.yunshang.haile_manager_android.ui.theme.BackgroundPageColor
import com.yunshang.haile_manager_android.ui.view.component.button.PrimaryButton

/**
 * Title : 自定义界面状态切换组件
 * Author: Lsy
 * Date: 2024/1/26 15:42
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
@Composable
fun PageStateComponent(
    pageState: PageState,
    initView: (@Composable (state: PageState) -> Unit)? = null,
    loadingView: (@Composable (state: PageState) -> Unit)? = null,
    loadFailureView: (@Composable (state: PageState) -> Unit)? = null,
    emptyDataView: (@Composable (state: PageState) -> Unit)? = null,
    requestPageData: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    when (pageState) {
        // 初始状态
        is PageState.InitState -> if (!pageState.hideContent) content() else {
            initView?.invoke(pageState) ?: initPage(pageState)
        }
        // 加载状态
        is PageState.Loading -> Box {
            if (!pageState.hideContent)
                content()
            loadingView?.invoke(pageState) ?: loadingPage(pageState)
        }
        // 加载失败状态
        is PageState.LoadFailure -> loadFailureView?.invoke(pageState) ?: loadFailurePage(
            pageState,
            requestPageData
        )
        // 空数据状态
        is PageState.EmptyData -> emptyDataView?.invoke(pageState) ?: emptyDataPage(
            pageState,
            requestPageData
        )

        else -> content()
    }
}


/**
 * 初始状态布局
 */
@Composable
fun initPage(state: PageState) {
    if (state is PageState.InitState) {
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
fun loadingPage(state: PageState) {
    if (state is PageState.Loading) {
        Dialog(onDismissRequest = { }) {
            Column(
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
fun loadFailurePage(state: PageState, requestPageData: (() -> Unit)? = null) {
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
                requestPageData?.invoke()
            }
        }
    }
}

/**
 * 空数据布局
 */
@Composable
fun emptyDataPage(state: PageState, requestPageData: (() -> Unit)? = null) {
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
                requestPageData?.invoke()
            }
        }
    }
}