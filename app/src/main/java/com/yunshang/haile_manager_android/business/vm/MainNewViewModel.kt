package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lsy.framelib.utils.AppPackageUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.data.common.Constants
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.ui.pages.HomePage
import com.yunshang.haile_manager_android.ui.pages.MinePage
import com.yunshang.haile_manager_android.ui.pages.MonitoringPage
import com.yunshang.haile_manager_android.ui.pages.StatisticsPage
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

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

    // 首页item列表
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

    // 当前选择的item
    var selectItem by mutableStateOf(mainItemList.first())

    // 是否显示服务器检查弹窗
    var showServiceCheckDialog by mutableStateOf(false).apply {
        // 一天显示一次
        this.value = if (Constants.needHintServiceUpdate
            && ((System.currentTimeMillis() - SPRepository.serviceCheckTime) / (3600 * 1000 * 24)) > 0
        ) {
            Constants.needHintServiceUpdate = false
            SPRepository.serviceCheckTime = System.currentTimeMillis()
            true
        } else false
    }

    // 是否显示更新弹窗
    var showUpdateAppDialog by mutableStateOf(false)

    // 更新数据
    var appVersion by mutableStateOf<AppVersionEntity?>(null)

    /**
     * 检测更新
     */
    fun checkVersion(context: Context) {
        launch({
            appVersion = ApiRepository.dealApiResult(
                mCommonRepo.appVersion(
                    "1.0.0"//AppPackageUtils.getVersionName(context)
                )
            )?.apply {
                showUpdateAppDialog = true
//                    if (this.forceUpdate) {// 强制更新
//                    true
//                } else if (this.needUpdate
//                    && !DateTimeUtils.isSameDay(
//                        Date(SPRepository.checkUpdateTime),
//                        Date()
//                    ) // 有更新且一天显示一次
//                ) {
//                    SPRepository.checkUpdateTime = System.currentTimeMillis()
//                    true
//                } else false
            }
        }, showLoading = false)
    }

    data class MainItemEntity(
        val iconResId: Int,
        val iconSelectResId: Int,
        val itemName: Int,
        var itemShow: Boolean = true,
        val itemContent: @Composable () -> Unit
    )
}