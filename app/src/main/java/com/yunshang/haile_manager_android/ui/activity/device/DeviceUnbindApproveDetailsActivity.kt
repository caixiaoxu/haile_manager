package com.yunshang.haile_manager_android.ui.activity.device

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceUnbindApproveDetailsViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceUnbindApproveDetailsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/16 18:11
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceUnbindApproveDetailsActivity :
    BaseBusinessActivity<ActivityDeviceUnbindApproveDetailsBinding, DeviceUnbindApproveDetailsViewModel>(
        DeviceUnbindApproveDetailsViewModel::class.java
    ) {
    override fun layoutId(): Int = R.layout.activity_device_unbind_approve_details

    override fun backBtn(): View = mBinding.barDeviceUnbindApproveDetailsTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = android.graphics.Color.WHITE

        mBinding.cvDeviceUnbindApproveDetailsContent.setContent {
            DeviceUnbindApproveDetailsContent()
        }
    }

    @Composable
    fun DeviceUnbindApproveDetailsContent() {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                ) {

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.page_bottom_h))
                    .background(Color.White)
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp)
            ) {

            }
        }
    }

    override fun initData() {
    }
}