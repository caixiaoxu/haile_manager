package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceUnbindApproveDetailsViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.databinding.ActivityDeviceUnbindApproveDetailsBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import com.yunshang.haile_manager_android.utils.StringUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

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

    override fun initIntent() {
        super.initIntent()
        mViewModel.approveId = IntentParams.CommonParams.parseId(intent)
    }

    override fun initView() {
        window.statusBarColor = android.graphics.Color.WHITE

        mBinding.cvDeviceUnbindApproveDetailsContent.setContent {
            DeviceUnbindApproveDetailsContent()
        }
    }

    @Composable
    fun DeviceUnbindApproveDetailsContent() {
        if (null != mViewModel.approveDetails) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    DeviceUnbindApproveDetailsInfoView()
                }
                if (UserPermissionUtils.hasDeviceUnbindPermission() && 1 == mViewModel.approveDetails?.status) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(id = R.dimen.page_bottom_h))
                            .background(Color.White)
                            .padding(start = 16.dp, top = 12.dp, end = 16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                mViewModel.disposeDeviceUnbind(
                                    this@DeviceUnbindApproveDetailsActivity,
                                    2
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            border = BorderStroke(1.dp, colorResource(id = R.color.color_black_25)),
                        ) {
                            Text(
                                text = stringResource(id = R.string.reject1),
                                fontSize = 17.sp,
                                color = colorResource(id = R.color.color_black_85)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                mViewModel.disposeDeviceUnbind(
                                    this@DeviceUnbindApproveDetailsActivity,
                                    1
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.colorPrimary),
                                contentColor = colorResource(id = R.color.colorPrimaryPress),
                                disabledContainerColor = colorResource(id = R.color.colorPrimary),
                                disabledContentColor = colorResource(id = R.color.colorPrimaryPress),
                            ),
                        ) {
                            Text(
                                text = stringResource(id = R.string.approve),
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DeviceUnbindApproveDetailsInfoView() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.icon_unbind_approve_detail_main),
                    contentDescription = null
                )
                Text(
                    mViewModel.approveDetails?.statusVal() ?: "",
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 24.sp,
                    color = colorResource(
                        id = R.color.colorPrimary
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(
                color = colorResource(id = R.color.color_black_10),
                thickness = 0.5.dp
            )

            Column(
                modifier = Modifier
                    .width(270.dp)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.device_name)}：",
                    mViewModel.approveDetails?.goodsName
                ) {
                    startActivity(
                        Intent(
                            this@DeviceUnbindApproveDetailsActivity,
                            DeviceDetailActivity::class.java
                        ).apply {
                            putExtra(
                                DeviceDetailActivity.GoodsId,
                                mViewModel.approveDetails?.goodsId
                            )
                        }
                    )
                }
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.imei)}：",
                    mViewModel.approveDetails?.imei
                ) {
                    StringUtils.copyToShear(mViewModel.approveDetails?.imei ?: "")
                }
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.device_category)}：",
                    mViewModel.approveDetails?.categoryName
                )
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.shop_name)}：",
                    mViewModel.approveDetails?.shopName
                )
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.pt)}：",
                    mViewModel.approveDetails?.positionName
                )
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.apply_user)}：",
                    mViewModel.approveDetails?.creatorAccount
                ) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data =
                        Uri.parse("tel:${mViewModel.approveDetails?.creatorAccount}")
                    startActivity(intent)
                }
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.apply_time)}：",
                    mViewModel.approveDetails?.createTime
                )
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.apply_operate)}：",
                    stringResource(id = R.string.device_unbind)
                )
                DeviceUnbindApproveDetailsItemView(
                    "${stringResource(id = R.string.reason_desc)}：",
                    mViewModel.approveDetails?.remark,
                )
                if (1 != mViewModel.approveDetails?.status) {
                    DeviceUnbindApproveDetailsItemView(
                        "${stringResource(id = R.string.dispose_time)}：",
                        mViewModel.approveDetails?.auditTime,
                    )
                }
            }
        }
    }

    @Composable
    fun DeviceUnbindApproveDetailsItemView(
        title: String,
        value: String?,
        onclick: (() -> Unit)? = null
    ) {
        if (!value.isNullOrEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.width(70.dp),
                    fontSize = 14.sp,
                    color = colorResource(
                        id = R.color.color_black_45
                    )
                )
                Text(
                    text = value,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            onclick?.invoke()
                        },
                    fontSize = 14.sp,
                    color = colorResource(
                        id = if (null != onclick) R.color.colorPrimary else R.color.color_black_85
                    )
                )
            }
        }
    }

    override fun initData() {
        mViewModel.requestData()
    }
}