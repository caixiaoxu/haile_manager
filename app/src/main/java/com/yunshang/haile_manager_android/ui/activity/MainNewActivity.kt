package com.yunshang.haile_manager_android.ui.activity

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.king.wechat.qrcode.WeChatQRCodeDetector
import com.lsy.framelib.utils.ActivityUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MainNewViewModel
import com.yunshang.haile_manager_android.ui.activity.base.BaseComposeActivity
import com.yunshang.haile_manager_android.ui.activity.base.PageState
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
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
            Box(
                modifier = Modifier
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .fillMaxSize()
            ) {
                mViewModel.selectItem.itemContent()
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
    }


    override fun onBackPressed() {
        ActivityUtils.doubleBack(this)
    }
}