package com.lsy.framelib.network.interceptors

import android.util.Log
import com.lsy.framelib.network.intfs.IInterceptor
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Title : 打印请求日志拦截器
 * Author: Lsy
 * Date: 2023/3/17 15:51
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class OkHttpLogInterceptor : IInterceptor {
    override fun getInterceptor(): Interceptor {
        val mHttpLogInter = HttpLoggingInterceptor { message ->
            Timber.d("HttpLogging=====$message")
        }
        mHttpLogInter.level = HttpLoggingInterceptor.Level.BODY
        return mHttpLogInter
    }

}