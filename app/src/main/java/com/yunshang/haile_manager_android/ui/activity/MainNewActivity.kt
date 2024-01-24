package com.yunshang.haile_manager_android.ui.activity

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.king.wechat.qrcode.WeChatQRCodeDetector
import com.lsy.framelib.utils.ActivityUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MainNewViewModel
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.ui.activity.base.BaseComposeActivity
import com.yunshang.haile_manager_android.ui.activity.base.PageState
import com.yunshang.haile_manager_android.ui.theme.PrimaryColor
import com.yunshang.haile_manager_android.ui.theme.PrimaryColor150
import com.yunshang.haile_manager_android.ui.view.component.WidgetState
import com.yunshang.haile_manager_android.ui.view.component.button.PrimaryButton
import org.opencv.OpenCV


class MainNewActivity : BaseComposeActivity<MainNewViewModel>(MainNewViewModel::class.java) {
    override var isFullScreen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化OpenCV
        OpenCV.initAsync(this)
        // 初始化WeChatQRCodeDetector
        WeChatQRCodeDetector.init(this)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    override fun content() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colorResource(id = R.color.page_bg),
            bottomBar = {
                MainBottomBar()
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .padding(bottom = innerPadding.calculateBottomPadding())
                        .fillMaxSize()
                ) {
                    mViewModel.selectItem.itemContent()
                }

                ServiceCheckDialog()
                UpdateAppDialog()
            }
        }
    }

    @Composable
    fun ServiceCheckDialog() {
        if (mViewModel.showServiceCheckDialog) {
            Dialog(onDismissRequest = { mViewModel.showServiceCheckDialog = false }) {
                Column(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.common_dialog_w), 394.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    AndroidView(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), factory = { ctx ->
                        WebView(ctx).apply {
                            this.webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView,
                                    request: WebResourceRequest
                                ): Boolean {
                                    view.loadUrl(request.url.toString())
                                    return true
                                }
                            }
                            loadUrl("https://notice.haier-ioc.com/notice.html ")
                        }
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        txt = stringResource(id = R.string.i_know),
                        modifier = Modifier.size(184.dp, 44.dp),
                        buttonState = WidgetState.EnableState
                    ) {
                        mViewModel.showServiceCheckDialog = false
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    @Preview
    @Composable
    fun UpdateAppDialog() {
        if (!mViewModel.showServiceCheckDialog && mViewModel.showUpdateAppDialog) {
            mViewModel.appVersion?.let { version ->
                Dialog(
                    onDismissRequest = { mViewModel.showUpdateAppDialog = false },
                    properties = DialogProperties(
                        dismissOnBackPress = !version.forceUpdate,
                        dismissOnClickOutside = !version.forceUpdate
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.common_dialog_w), 394.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .height(155.dp)
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.mipmap.icon_update_app_top),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 16.dp)
                            ) {
                                Spacer(modifier = Modifier.height(48.dp))
                                Text(
                                    text = stringResource(id = R.string.new_version),
                                    fontSize = 24.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = version.versionName,
                                    fontSize = 12.sp,
                                    color = PrimaryColor,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PrimaryColor150)
                                        .border(0.5.dp, PrimaryColor, RoundedCornerShape(14.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(id = R.string.update_content),
                                fontSize = 14.sp,
                                color = colorResource(
                                    id = R.color.common_sub_txt_color
                                )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Text(
                                    text = version.updateLog,
                                    fontSize = 14.sp,
                                    color = Color(0xD8333333)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun MainBottomBar() {
        BottomAppBar(
            containerColor = colorResource(id = R.color.dividing_line_color),
            contentColor = colorResource(id = R.color.dividing_line_color),
        ) {
            mViewModel.mainItemList.filter { it.itemShow }.forEachIndexed { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            mViewModel.selectItem = item
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = if (item == mViewModel.selectItem) item.iconSelectResId else item.iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(if (0 != index || item != mViewModel.selectItem) 24.dp else 40.dp),
                    )
                    if (0 != index || item != mViewModel.selectItem) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(id = item.itemName),
                            fontSize = 10.sp,
                            color = colorResource(id = R.color.common_txt_color),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }

    override fun initEvent() {
        super.initEvent()

        mSharedViewModel.hasDataStatisticsListPermission.observe(this) {
            changeItemShow(1, it)
        }
        mSharedViewModel.hasDeviceMonitoringPermission.observe(this) {
            changeItemShow(2, it)
        }
    }

    private fun changeItemShow(index: Int, show: Boolean) {
        mViewModel.mainItemList[index].itemShow = show
        if (mViewModel.selectItem == mViewModel.mainItemList[index]) {
            mViewModel.selectItem = mViewModel.mainItemList.first()
        }
    }

    override fun requestData() {
        super.requestData()
        mViewModel.pageState = PageState.LoadData

        // 如果用户信息为空，重新请求
        if (null == SPRepository.userInfo) {
            mSharedViewModel.requestUserInfoAsync()
        }

        // 刷新权限
        mSharedViewModel.requestUserPermissionsAsync()

        // 检测更新
        mViewModel.checkVersion(this)
    }

    override fun onBackPressed() {
        ActivityUtils.doubleBack(this)
    }
}