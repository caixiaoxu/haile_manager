package com.lsy.framelib.utils

import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.lsy.framelib.R

/**
 * Title : 控件工具类
 * Author: Lsy
 * Date: 2023/4/3 17:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object ActivityUtils {
    private const val TIME_INTERVAL = 2000 // 时间间隔
    private var mBackPressedTime: Long = 0

    /**
     * 添加回退监听
     * @param activity
     * @param backPressedDispatcher 回退处理器
     */
    fun addDoubleBack(activity: AppCompatActivity, backPressedDispatcher: OnBackPressedDispatcher) {
        backPressedDispatcher.addCallback(activity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                doubleBack(activity)
            }
        })
    }

    /**
     * 双击回退
     * @param activity Activity
     */
    fun doubleBack(activity: Activity) {
        if (mBackPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
            AppManager.finishAllActivity()
            return
        }
        SToast.showToast(activity, R.string.double_back_hint)
        mBackPressedTime = System.currentTimeMillis()
    }
}