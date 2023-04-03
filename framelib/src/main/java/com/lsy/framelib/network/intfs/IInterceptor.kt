package com.lsy.framelib.network.intfs

import okhttp3.Interceptor

/**
 * Title : 拦截器对外暴露接口
 * Author: Lsy
 * Date: 2023/3/17 15:53
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface IInterceptor {

    /**
     * 创建拦截器
     */
    fun getInterceptor(): Interceptor
}