package com.yunshang.haile_manager_android.ui.activity.personal

import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.lsy.framelib.utils.AppPackageUtils
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.SystemPermissionHelper
import com.yunshang.haile_manager_android.BR
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.vm.VersionViewModel
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.model.OnDownloadProgressListener
import com.yunshang.haile_manager_android.databinding.ActivityVersionBinding
import com.yunshang.haile_manager_android.ui.activity.BaseBusinessActivity
import timber.log.Timber
import java.io.File

class VersionActivity : BaseBusinessActivity<ActivityVersionBinding, VersionViewModel>(
    VersionViewModel::class.java, BR.vm
) {

    // 权限
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.any { it }) {
                showProgress(true)
                // 授权权限成功
                mViewModel.downLoadApk(object : OnDownloadProgressListener {
                    override fun onProgress(curSize: Long, totalSize: Long) {
                        (curSize * 100 / totalSize).toInt().let { precent ->
                            mBinding.pbVersionUpdateProgress.progress = precent
                            mBinding.tvUpdateProgressValue.text = "${precent}%"
                        }
                    }

                    override fun onSuccess(file: File) {
                        Timber.i("文件下载完成：${file.path}")
                        AppPackageUtils.installApk(this@VersionActivity, file)
                        showProgress(false)
                    }

                    override fun onFailure(e: Throwable) {
                        Timber.i("文件下载失败：${e.message}")
                        showProgress(false)
                    }
                })
            } else {
                // 授权失败
                SToast.showToast(this, R.string.empty_permission)
            }
        }

    override fun layoutId(): Int = R.layout.activity_version

    override fun backBtn(): View = mBinding.barVersionTitle.getBackBtn()
    override fun initEvent() {
        super.initEvent()
        IntentParams.VersionParams.parseVersionInfo(intent)?.let {
            mViewModel.appVersion.postValue(it)
        }
    }

    override fun initView() {
        window.statusBarColor = Color.WHITE

        mBinding.btnVersionUpdate.setOnClickListener {
            requestPermissions.launch(
                SystemPermissionHelper.readWritePermissions()
                    .plus(SystemPermissionHelper.installPackagesPermissions())
            )
        }
    }

    override fun initData() {
        mViewModel.curVersion.postValue(
            "${getString(R.string.current_version)}V${
                AppPackageUtils.getVersionName(
                    this
                )
            }"
        )
    }

    private fun showProgress(isShow: Boolean) {
        mBinding.pbVersionUpdateProgress.visibility = if (isShow) View.VISIBLE else View.GONE
        mBinding.tvUpdateProgressValue.visibility = if (isShow) View.VISIBLE else View.GONE
        if (isShow) {
            mBinding.tvUpdateProgressValue.setText(R.string.in_prepare)
        }
        mBinding.btnVersionUpdate.visibility = if (isShow) View.GONE else View.VISIBLE
    }
}