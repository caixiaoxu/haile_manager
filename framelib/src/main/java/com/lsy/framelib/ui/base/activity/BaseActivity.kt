package com.lsy.framelib.ui.base.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.lsy.framelib.ui.weight.loading.LoadDialogMgr
import com.lsy.framelib.utils.AppManager
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StatusBarUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Title : Activity基类
 * Author: Lsy
 * Date: 2023/3/16 14:25
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class BaseActivity : AppCompatActivity() {
    // 是否全屏显示(是否忽略状态栏高度)
    protected open fun isFullScreen() = false

    // 是否高度(状态栏字体颜色)
    protected open fun isDark() = true

    // 屏幕方向
    protected open fun orientation() = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.addActivity(activityTag(), this)
        // 设置状态栏的距离
        setRootViewPT()
        // 屏幕方向
        setScreenOrientation()

        // 回退按钮
        backBtn()?.setOnClickListener { onBackListener() }
    }

    /**
     * 状态栏文字颜色
     */
    protected fun initStatusBarTxtColor(root: View) {
        StatusBarUtils.setStatusBarDarkTheme(root, isDark())
    }

    /**
     * Activity Tag
     */
    protected open fun activityTag(): String? = null

    /**
     * 回退按钮
     */
    open fun backBtn(): View? = null

    /**
     * 设置内容布局的上边距(状态栏的距离)
     */
    private fun setRootViewPT() {
        if (!isFullScreen()) {
            val rootView = window.decorView.findViewById<FrameLayout>(android.R.id.content)
            rootView.setPadding(0, StatusBarUtils.getStatusBarHeight(), 0, 0)
        }
    }

    /**
     * 设置屏幕方向
     */
    private fun setScreenOrientation() {
        orientation().let {
            if (this.resources.configuration.orientation != it) {
                requestedOrientation = it
            }
        }
    }

    /**
     * ViewModel异常处理方法
     */
    fun launch(
        block: suspend () -> Unit,           // 异步操作
        error: (suspend (Throwable) -> Unit)? = null,  // 操作异常
        complete: (suspend () -> Unit)? = null,        // 操作完成
        showLoading: Boolean = true          // 是否显示加载弹窗
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            //显示加载弹窗
            if (showLoading) {
                withContext(Dispatchers.Main) {
                    LoadDialogMgr.showLoadingDialog(this@BaseActivity)
                }
            }

            try {
                block()
            } catch (e: Exception) {
                error?.invoke(e) ?: withContext(Dispatchers.Main) {
                    e.message?.let { it1 -> SToast.showToast(msg = it1) }
                    Timber.d("请求失败或异常$e")
                }
            } finally {
                complete?.invoke() ?: withContext(Dispatchers.Main) {
                    Timber.d("请求结束")
                }
            }
            //隐藏加载弹窗
            if (showLoading)
                withContext(Dispatchers.Main) {
                    LoadDialogMgr.hideLoadingDialog(this@BaseActivity)
                }
        }
    }

    /**
     * 默认的回退事件
     */
    protected open fun onBackListener() {
        finish()
    }

    override fun onDestroy() {
        AppManager.finishActivity(activityTag(), this)
        super.onDestroy()
    }
}