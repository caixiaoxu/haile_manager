package com.lsy.framelib.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lsy.framelib.business.bridge.SharedViewModel

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/16 14:26
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
open class BaseFragment : Fragment() {

    // 贯穿整个项目的（只会让App(Application)初始化一次）
    protected val mSharedViewModel: SharedViewModel by lazy {
        getAppViewModelProvider().get(SharedViewModel::class.java)
    }

    // 给当前BaseFragment用的【共享区域的ViewModel】
    protected fun getAppViewModelProvider(): ViewModelProvider {
        return (requireActivity().applicationContext as BaseApp).getAppViewModelProvider()
    }

    // 给所有的 子fragment提供的函数，可以顺利的拿到 ViewModel 【非共享区域的ViewModel】
    protected fun getFragmentViewModelProvider(fragment: Fragment): ViewModelProvider {
        return ViewModelProvider(fragment, fragment.defaultViewModelProviderFactory)
    }

    // 给所有的 子fragment提供的函数，可以顺利的拿到 ViewModel 【非共享区域的ViewModel】
    protected fun getActivityViewModelProvider(activity: AppCompatActivity): ViewModelProvider {
        return ViewModelProvider(activity, activity.defaultViewModelProviderFactory)
    }
}