package com.shangyun.haile_manager_android

import com.lsy.framelib.network.ApiService
import com.lsy.framelib.network.DefaultOkHttpClient
import com.lsy.framelib.network.interceptors.BasicParamsInterceptor
import com.lsy.framelib.network.interceptors.OkHttpBodyLogInterceptor
import com.lsy.framelib.network.interceptors.OkHttpHeadLogInterceptor
import com.lsy.framelib.network.interceptors.ResponseInterceptor
import com.lsy.framelib.ui.base.BaseApp
import com.shangyun.haile_manager_android.data.model.SPRepository
import com.shangyun.haile_manager_android.log.CrashReportingTree
import timber.log.Timber


/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/16 14:48
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class BusinessApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        initLog()
        initNetwork()
    }

    fun initNetwork() {
        // 初始化Retrofit，并配置拦截器
        ApiService
            .registerClient(
                DefaultOkHttpClient()
                    .setInterceptors(
                        arrayOf(
                            OkHttpHeadLogInterceptor().getInterceptor(),
                            OkHttpBodyLogInterceptor().getInterceptor(),
                            BasicParamsInterceptor({ dealHttpHeader() },
                                { dealHttpParams(it) }).getInterceptor(),
                            ResponseInterceptor().getInterceptor()
                        )
                    )
            )
    }

    /**
     * 处理公共Header信息
     */
    private fun dealHttpHeader(): HashMap<String, String> {
        return HashMap<String, String>().apply {
            SPRepository.loginInfo?.token?.let { put("Authorization", it) }
        }
    }

    /**
     * 处理公共Http参数
     */
    private fun dealHttpParams(params: Map<String, String>) {
    }

    /**
     * 初始化日志
     */
    private fun initLog() {
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())
    }

}