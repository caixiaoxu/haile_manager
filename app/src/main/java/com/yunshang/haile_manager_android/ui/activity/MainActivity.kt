package com.yunshang.haile_manager_android.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.king.wechat.qrcode.WeChatQRCodeDetector
import com.lsy.framelib.utils.ActivityUtils
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.AppPackageUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.BuildConfig
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MainViewModel
import com.yunshang.haile_manager_android.data.common.Constants
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.data.rule.CommonDialogItemParam
import com.yunshang.haile_manager_android.databinding.ActivityMainBinding
import com.yunshang.haile_manager_android.databinding.ItemEnvSelectBinding
import com.yunshang.haile_manager_android.ui.view.adapter.ViewBindingAdapter.visibility
import com.yunshang.haile_manager_android.ui.view.dialog.CommonNewBottomSheetDialog
import com.yunshang.haile_manager_android.ui.view.dialog.ServiceCheckDialog
import com.yunshang.haile_manager_android.ui.view.dialog.UpdateAppDialog
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import com.yunshang.haile_manager_android.utils.UserPermissionUtils
import org.opencv.OpenCV
import timber.log.Timber
import java.io.File
import java.util.*


class MainActivity :
    BaseBusinessActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class.java, BR.vm) {

    override fun isFullScreen(): Boolean = true

    override fun layoutId(): Int = R.layout.activity_main

    override fun onBackListener() {
        ActivityUtils.doubleBack(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化OpenCV
        OpenCV.initAsync(this)
        // 初始化WeChatQRCodeDetector
        WeChatQRCodeDetector.init(this)
    }

    override fun initView() {

        if (BuildConfig.DEBUG) {
            mBinding.ibChangeEnv.visibility(true)
            mBinding.ibChangeEnv.setOnClickListener {
                val envList = listOf(
                    CommonDialogItemParam(0, "120", "http://192.168.5.120:9083"),
                    CommonDialogItemParam(1, "130", "http://192.168.5.130:9083"),
                    CommonDialogItemParam(2, "140", "http://192.168.5.140:9083"),
                    CommonDialogItemParam(3, "20", "http://192.168.5.20:9083"),
                    CommonDialogItemParam(4, "预发", "https://pre-merchant.haier-ioc.com"),
                )
                CommonNewBottomSheetDialog.Builder<CommonDialogItemParam, ItemEnvSelectBinding>(
                    "环境切换", envList, mustSelect = false, buildItemView = { _, data, _ ->
                        DataBindingUtil.inflate<ItemEnvSelectBinding?>(
                            LayoutInflater.from(this@MainActivity),
                            R.layout.item_env_select,
                            null,
                            false
                        ).apply {
                            child = data
                        }
                    }
                ) {
                    SPRepository.selectEnv = envList.find { item -> item.commonItemSelect }?.origin
                    AppManager.finishAllActivity()
                }.build().show(supportFragmentManager)
            }
        }
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasDataStatisticsListPermission.observe(this) {
            val percent: Float = if (it) {
                if (UserPermissionUtils.hasDeviceMonitoringPermission()) 0.25f else 0.33f
            } else {
                if (UserPermissionUtils.hasDeviceMonitoringPermission()) 0.33f else 0.5f
            }

            mBinding.glMainTabHomeEnd.setGuidelinePercent(percent)
            mBinding.rbMainTabStatistics.visibility = if (it) View.VISIBLE else View.GONE
        }
        mSharedViewModel.hasDeviceMonitoringPermission.observe(this) {
            val percent: Float = if (it) {
                if (UserPermissionUtils.hasDataStatisticsListPermission()) 0.25f else 0.33f
            } else {
                if (UserPermissionUtils.hasDataStatisticsListPermission()) 0.33f else 0.5f
            }
            mBinding.glMainTabHomeEnd.setGuidelinePercent(percent)
            mBinding.rbMainTabMonitoring.visibility = if (it) View.VISIBLE else View.GONE
        }

        mViewModel.checkId.observe(this) {
            showChildFragment(it)
        }
    }

    /**
     * 显示子布局
     */
    private fun showChildFragment(id: Int) {

        // 隐藏之前的界面
        mViewModel.curFragmentTag?.let {
            supportFragmentManager.findFragmentByTag(it)?.let { fragment ->
                supportFragmentManager.beginTransaction().hide(fragment).commit()
            }
        } ?: run {
            supportFragmentManager.fragments.forEach { fragment ->
                supportFragmentManager.beginTransaction().hide(fragment).commit()
            }
        }

        mViewModel.fragments[id]?.let {
            val fragmentName = it.javaClass.name
            supportFragmentManager.findFragmentByTag(fragmentName)?.let { fragment ->
                supportFragmentManager.beginTransaction().show(fragment).commit()
                if (it != fragment) {
                    mViewModel.fragments[id] = fragment
                }
            } ?: supportFragmentManager.beginTransaction()
                .add(R.id.fl_main_controller, it, fragmentName)
                .commit()
            mViewModel.curFragmentTag = fragmentName
        }
    }

    override fun initData() {
        // 如果用户信息为空，重新请求
        if (null == SPRepository.userInfo) {
            mSharedViewModel.requestUserInfoAsync()
        }

        // 刷新权限
        mSharedViewModel.requestUserPermissionsAsync()


        if (Constants.needHintServiceUpdate
            && ((System.currentTimeMillis() - SPRepository.serviceCheckTime) / (3600 * 1000 * 24)) > 0
        ) {
            Constants.needHintServiceUpdate = false
            SPRepository.serviceCheckTime = System.currentTimeMillis()

            ServiceCheckDialog() {
                checkUpdate()
            }.show(supportFragmentManager)
        } else {
            checkUpdate()
        }
    }

    private fun checkUpdate() {
        mViewModel.checkVersion(this) {
            if (it.forceUpdate) {
                // 强制更新
                showUpdateDialog(it, true)
                return@checkVersion
            } else if (it.needUpdate
                && !DateTimeUtils.isSameDay(Date(SPRepository.checkUpdateTime), Date())
            ) {
                // 非强制更新并有更新,每天一次
                showUpdateDialog(it, false)
                SPRepository.checkUpdateTime = System.currentTimeMillis()
            }
        }
    }

    private fun showUpdateDialog(appVersion: AppVersionEntity, isForce: Boolean) {
        val updateAppDialog = UpdateAppDialog.Builder(appVersion).apply {
            isCancelable = !isForce
            positiveClickListener = { callBack ->
                // 授权权限成功
                mViewModel.downLoadApk(appVersion, object : OnDownloadProgressListener {

                    override fun onProgress(curSize: Long, totalSize: Long) {
                        callBack(curSize, totalSize, 0)
                    }

                    override fun onSuccess(file: File) {
                        Timber.i("文件下载完成：${file.path}")
                        callBack(0, 0, 1)
                        AppPackageUtils.installApk(this@MainActivity, file)
                    }

                    override fun onFailure(e: Throwable) {
                        Timber.i("文件下载失败：${e.message}")
                        callBack(0, 0, -1)
                    }
                })
            }
        }.build()
        updateAppDialog.show(supportFragmentManager)
    }
}