package com.yunshang.haile_manager_android.ui.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MainNewViewModel
import com.yunshang.haile_manager_android.ui.activity.base.BaseComposeActivity
import com.yunshang.haile_manager_android.ui.activity.base.PageState


class MainNewActivity : BaseComposeActivity<MainNewViewModel>(MainNewViewModel::class.java) {
    override var isFullScreen: Boolean = true

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
            Box(modifier = Modifier.padding(innerPadding)) {

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

        }
    }

    override fun requestData() {
        super.requestData()
        mViewModel.pageState = PageState.LoadData
    }
}