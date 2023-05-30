package com.lsy.framelib.ui.base

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.lsy.framelib.data.constants.Constants

/**
 * Title : Application基类
 * Author: Lsy
 * Date: 2023/3/16 14:30
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
open class BaseApp : Application(), ViewModelStoreOwner {

    private val mAppViewModelStore: ViewModelStore by lazy { ViewModelStore() }
    private val mFactory: ViewModelProvider.Factory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
        //公共记录
        Constants.APP_CONTEXT = applicationContext
    }

    // 专门给 BaseActivity 与 BaseFragment 用的
    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, mFactory);
    }

    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore
}