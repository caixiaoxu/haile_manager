package com.lsy.framelib.network.intfs

import okhttp3.OkHttpClient

/**
 * Title : 对外暴露接口
 * Author: Lsy
 * Date: 2023/3/17 11:39
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
interface IOkHttpClient {

    // 获取OkHttp实例
    fun getClient(): OkHttpClient
}