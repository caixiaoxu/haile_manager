package com.lsy.framelib.network.interceptors

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
class OkHttpBodyLogInterceptor : IInterceptor {
    override fun getInterceptor(): Interceptor {
        val mHttpBodyLogInter = HttpLoggingInterceptor { message ->
            Timber.i("HttpBodyLogInter=====$message")
        }
        mHttpBodyLogInter.level = HttpLoggingInterceptor.Level.BODY
        return mHttpBodyLogInter
    }

}