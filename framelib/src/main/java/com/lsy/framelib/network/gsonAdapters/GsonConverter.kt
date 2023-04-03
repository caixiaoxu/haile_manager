package com.lsy.framelib.network.gsonAdapters

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lsy.framelib.network.response.ResponseWrapper
import java.util.*

/**
 * Title : Gson转换器
 * Author: Lsy
 * Date: 2023/3/17 14:54
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class GsonConverter {
    /**
     * 自定义Gson
     */
    fun <T> createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(String::class.java, NullStringAdapter())
            .registerTypeAdapter(Int::class.java, IntDeserializer())
            .registerTypeAdapter(Long::class.java, LongDeserializer())
            .registerTypeAdapter(Double::class.java, DoubleDeserializer())
            .registerTypeAdapter(Date::class.java, DateSerializer())
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .registerTypeAdapter(ResponseWrapper::class.java, ResponseWrapperDeserializer<T>())
            .create()
    }
}