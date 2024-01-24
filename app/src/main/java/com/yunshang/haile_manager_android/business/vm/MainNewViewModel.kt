package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.luck.picture.lib.utils.ToastUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CommonService
import com.yunshang.haile_manager_android.business.vm.base.BaseComposeViewModel
import com.yunshang.haile_manager_android.data.common.Constants
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.ui.pages.HomePage
import com.yunshang.haile_manager_android.ui.pages.MinePage
import com.yunshang.haile_manager_android.ui.pages.MonitoringPage
import com.yunshang.haile_manager_android.ui.pages.StatisticsPage
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

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
        })
    }

    var isUpdating by mutableStateOf(false)
    var updateCurSize by mutableStateOf(0L)
    var updateTotalSize by mutableStateOf(0L)

    fun downLoadApk(
        installApk: (apk: File) -> Unit,
    ) {
        if (appVersion?.updateUrl.isNullOrEmpty()) {
            return
        }

        launch({
            isUpdating = true
            ApiRepository.downloadFile(
                "https://cos.pgyer.com/53c56f82c4fe958a8efd8dd1ca366542.apk?sign=c0033d978889d5128d22b31fc2057918&sign2=b4033e6616afbd2ac3b3689c6e52179b&t=1706093099&response-content-disposition=attachment%3Bfilename%3D%22%E6%B5%B7%E4%B9%90%E7%AE%A1%E5%AE%B6_2.2.3.apk%22",//appVersion!!.updateUrl,
                "${appVersion!!.name}${appVersion!!.versionName}.apk",
                object : OnDownloadProgressListener {

                    override fun onProgress(curSize: Long, totalSize: Long) {
                        updateCurSize = curSize
                        updateTotalSize = totalSize
                    }

                    override fun onSuccess(file: File) {
                        Timber.i("文件下载完成：${file.path}")
                        isUpdating = false
                        showUpdateAppDialog = false
                        installApk(file)
                        if (appVersion!!.forceUpdate) {
                            AppManager.finishAllActivity()
                        }
                    }

                    override fun onFailure(e: Throwable) {
                        Timber.i("文件下载失败：${e.message}")
                        SToast.showToast(msg = "下载失败")
                        isUpdating = false
                    }
                }
            )
        }, {
            withContext(Dispatchers.Main) {
                SToast.showToast(msg = "下载失败")
            }
            isUpdating = false
        })
    }

    data class MainItemEntity(
        val iconResId: Int,
        val iconSelectResId: Int,
        val itemName: Int,
        var itemShow: Boolean = true,
        val itemContent: @Composable () -> Unit
    )
}