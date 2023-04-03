package com.lsy.framelib.network.gsonAdapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.lsy.framelib.network.response.ResponseWrapper
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Title : 处理自定义接口数据外层json格式
 * Author: Lsy
 * Date: 2023/3/17 15:40
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ResponseWrapperDeserializer<T> : JsonDeserializer<ResponseWrapper<T>> {
    /**
     * 反序列化接口外层json数据
     */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ResponseWrapper<T> {
        val jsonObj = json.asJsonObject
        val code = jsonObj.get("code").asInt
        val msg = jsonObj.get("message")?.asString ?: ""
        val version = jsonObj.get("version")?.asString ?: ""
        val timestamp = jsonObj.get("timestamp")?.asLong ?: 0
        val data = context.deserialize<T>(
            jsonObj.get("data"),
            (typeOfT as ParameterizedType).actualTypeArguments[0]
        )
        return ResponseWrapper(code, msg, version, timestamp, data)
    }
}