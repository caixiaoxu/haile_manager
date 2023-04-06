package com.lsy.framelib.utils.gson

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.*

/**
 * Title :
 * Author: Lsy
 * Date: 2023/3/17 15:15
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DateSerializer : JsonSerializer<Date> {
    // 序列化
    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return if (src == null) null else JsonPrimitive(src.time / 1000)
    }
}