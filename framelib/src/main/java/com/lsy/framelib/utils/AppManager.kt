package com.lsy.framelib.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*
import kotlin.system.exitProcess

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/7 10:08
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object AppManager {
    // 存储所有打开的activity
    private val activityStack: Stack<Activity> by lazy {
        Stack<Activity>()
    }

    private val activityMap: MutableMap<String, Stack<Activity>> by lazy {
        mutableMapOf()
    }

    /**
     * 加入Activity到堆栈
     */
    fun addActivity(tag: String? = null, activity: Activity) {
        activityStack.add(activity)
        tag?.let {
            if (activityMap[tag].isNullOrEmpty()) {
                activityMap[tag] = Stack<Activity>()
            }
            activityMap[tag]?.add(activity)
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? = activityStack.lastElement()

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        finishActivity(activityStack.lastElement())
    }

    /**
     *
     */
    fun finishActivity(activity: Activity?) {
        activity?.let {
            if (!it.isDestroyed) {
                activityStack.remove(activity)
                activity.finish()
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(tag: String? = null, activity: Activity?) {
        finishActivity(activity)
        tag?.let { activityMap.remove(tag) }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(tag: String? = null, cls: Class<*>) {
        for (activity in activityStack) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
        tag?.let { activityMap.remove(tag) }
    }

    /**
     * 结束全部Activity
     */
    fun finishAllActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
        activityMap.clear()
    }

    /**
     * 结束指定标签下的Activity
     */
    fun finishAllActivityForTag(tag: String) {
        activityMap[tag]?.let {
            for (activity in it) {
                finishActivity(activity)
            }
            it.clear()
        }
        activityMap.remove(tag)
    }

    /**
     * 退出应用程序
     */
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(context.packageName)
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}