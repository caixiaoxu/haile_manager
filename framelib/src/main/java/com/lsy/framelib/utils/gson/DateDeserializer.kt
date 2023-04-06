package com.lsy.framelib.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.util.*

/**
 * Title : 处理Date类型json
 * Author: Lsy
 * Date: 2023/3/17 15:17
 * Version:
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DateDeserializer : JsonDeserializer<Date> {
    /**
     * 对Date数据类型反序列化的时间戳转换到精确到毫秒
     */
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date? {
        return if (json == null || json.asLong == 0L) null else Date(json.asLong * 1000)
    }
}