package com.yunshang.haile_manager_android.ui.activity

import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import com.lsy.framelib.utils.ActivityUtils
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.MainViewModel
import com.yunshang.haile_manager_android.data.common.Constants
import com.yunshang.haile_manager_android.data.entities.AppVersionEntity
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import com.yunshang.haile_manager_android.data.model.SPRepository
import com.yunshang.haile_manager_android.databinding.ActivityMainBinding
import com.yunshang.haile_manager_android.ui.fragment.DataStatisticsFragment
import com.yunshang.haile_manager_android.ui.fragment.HomeFragment
import com.yunshang.haile_manager_android.ui.fragment.PersonalFragment
import com.yunshang.haile_manager_android.ui.view.dialog.ServiceCheckDialog
import com.yunshang.haile_manager_android.ui.view.dialog.UpdateAppDialog
import com.lsy.framelib.utils.AppPackageUtils
import com.yunshang.haile_manager_android.utils.DateTimeUtils
import timber.log.Timber
import java.io.File
import java.util.*


class MainActivity :
    BaseBusinessActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class.java, BR.vm) {

    // 当前的fragment
    private var curFragment: Fragment? = null

    private val fragments = SparseArray<Fragment>(3).apply {
        put(R.id.rb_main_tab_home, HomeFragment())
        put(R.id.rb_main_tab_statistics, DataStatisticsFragment())
        put(R.id.rb_main_tab_personal, PersonalFragment())
    }

    override fun isFullScreen(): Boolean = true

    override fun layoutId(): Int = R.layout.activity_main

    override fun onBackListener() {
        ActivityUtils.doubleBack(this)
    }

    override fun initView() {
    }

    override fun initEvent() {
        super.initEvent()
        mSharedViewModel.hasDataStatisticsListPermission.observe(this) {
            mBinding.glMainTabHomeEnd.setGuidelinePercent(if (it) 0.33f else 0.5f)
            mBinding.rbMainTabStatistics.visibility = if (it) View.VISIBLE else View.GONE
        }

        // 如果用户信息为空，重新请求
        if (null == SPRepository.userInfo) {
            mSharedViewModel.requestUserInfoAsync()
        }

        // 如果权限数据为空，重新请求
        mSharedViewModel.requestUserPermissionsAsync()

        mViewModel.checkId.observe(this) {
            showChildFragment(it)
        }
    }

    /**
     * 显示子布局
     */
    private fun showChildFragment(id: Int) {
        curFragment?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }

        fragments[id]?.let {
            if (it.isAdded) {
                supportFragmentManager.beginTransaction().show(it).commit()
            } else {
                supportFragmentManager.beginTransaction().add(R.id.fl_main_controller, it)
                    .commit()
            }
            curFragment = it
        }
    }

    override fun initData() {
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