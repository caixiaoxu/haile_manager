package com.yunshang.haile_manager_android.business.vm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.ui.pages.HomePage
import com.yunshang.haile_manager_android.ui.pages.MinePage
import com.yunshang.haile_manager_android.ui.pages.MonitoringPage
import com.yunshang.haile_manager_android.ui.pages.StatisticsPage
import com.yunshang.haile_manager_android.utils.UserPermissionUtils

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 17:02
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class MainNewViewModel : BaseComposeViewModel() {
    private val mCommonRepo = ApiRepository.apiClient(CommonService::class.java)
    val mainItemList = mutableStateListOf<MainItemEntity>().apply {
        addAll(
            listOf(
                MainItemEntity(
                    R.mipmap.icon_main_tab_home_uncheck,
                    R.mipmap.icon_main_tab_home_check,
                    R.string.main_tab_home
                ) { HomePage() },
                MainItemEntity(
                    R.mipmap.icon_main_tab_home_monitoring_uncheck,
                    R.mipmap.icon_main_tab_home_monitoring_check,
                    R.string.monitoring,
                    UserPermissionUtils.hasDeviceMonitoringPermission()
                ) { MonitoringPage() },
                MainItemEntity(
                    R.mipmap.icon_main_data_statistics_uncheck,
                    R.mipmap.icon_main_data_statistics_check,
                    R.string.statistics,
                    UserPermissionUtils.hasDataStatisticsListPermission()
                ) { StatisticsPage() },
                MainItemEntity(
                    R.mipmap.icon_main_tab_personal_uncheck,
                    R.mipmap.icon_main_tab_personal_check,
                    R.string.main_tab_personal
                ) { MinePage() }
            ))
    }

    var selectItem by mutableStateOf(mainItemList.first())

    data class MainItemEntity(
        val iconResId: Int,
        val iconSelectResId: Int,
        val itemName: Int,
        var itemShow: Boolean = true,
        val itemContent: @Composable () -> Unit
    )
}