package com.yunshang.haile_manager_android.ui.activity.device

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.DeviceUnbindAuditViewModel
import com.yunshang.haile_manager_android.databinding.ActivityDeviceUnbindAuditBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity

class DeviceUnbindAuditActivity :
    BaseBusinessActivity<ActivityDeviceUnbindAuditBinding, DeviceUnbindAuditViewModel>(
        DeviceUnbindAuditViewModel::class.java, BR.vm
    ) {

    override fun layoutId(): Int = R.layout.activity_device_unbind_audit

    override fun backBtn(): View = mBinding.barDeviceUnbindAuditTitle.getBackBtn()

    override fun initView() {
        window.statusBarColor = android.graphics.Color.WHITE

        mBinding.cvDeviceUnbindAuditContent.setContent {
            DeviceUnbindAuditContent()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DeviceUnbindAuditContent() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(top = 10.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    Row {
                        Text(
                            text = "*",
                            modifier = Modifier
                                .width(16.dp)
                                .padding(end = 2.dp)
                                .align(alignment = Alignment.CenterVertically),
                            color = colorResource(id = R.color.color_ff5219),
                            fontSize = 17.sp,
                            textAlign = TextAlign.End,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "标题文字",
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = colorResource(id = R.color.color_black_85),
                            fontSize = 17.sp,
                            lineHeight = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides TextSelectionColors(
                            colorResource(id = R.color.colorPrimary),
                            colorResource(id = R.color.colorPrimaryBg),
                        )
                    ) {
                        BasicTextField(
                            value = mViewModel.auditContent,
                            onValueChange = {
                                mViewModel.auditContent =
                                    if (it.length <= 100) it else it.substring(0, 100)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 64.dp)
                                .padding(start = 16.dp)
                                .align(Alignment.Start),
                            textStyle = TextStyle.Default.copy(
                                color = colorResource(id = R.color.color_black_85),
                                fontSize = 17.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Start,
                            ),
                            cursorBrush = SolidColor(colorResource(id = R.color.colorPrimary)),
                        ) { innerTextField ->
                            if (mViewModel.auditContent.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.please_input),
                                    modifier = Modifier.fillMaxWidth(),
                                    style = LocalTextStyle.current.copy(
                                        color = colorResource(id = R.color.color_black_45),
                                        fontSize = 17.sp,
                                        lineHeight = 22.sp,
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                    Text(
                        text = "${mViewModel.auditContent.length}/100",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        color = colorResource(id = R.color.color_black_45)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.page_bottom_h))
                    .background(Color.White)
                    .padding(14.dp, 8.dp, 14.dp)
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }

    override fun initData() {
    }
}